package com.example.servicefusiontechnicaltest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Sort a list of Person objects and display them, oldest at the top
 *
 * Created by Chad Schultz on 1/30/2016.
 */
public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
    private List<Person> mItems;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolderOnClickListener mListener;
        public TextView mNameTextView;
        public TextView mBirthDateTextView;
        public TextView mZipCodeTextView;

        public ViewHolder(final View v, ViewHolderOnClickListener listener) {
            super(v);
            mListener = listener;
            v.setOnClickListener(this); // need to do this for every clickable view
            mNameTextView = (TextView) v.findViewById(R.id.name_textview);
            mBirthDateTextView = (TextView) v.findViewById(R.id.birthdate_textview);
            mZipCodeTextView = (TextView) v.findViewById(R.id.zip_code_textview);
        }

        @Override
        public void onClick(View v) {
            // Can check the view for class or other identifying criteria and
            // call different listener methods based on that
            mListener.onClickItemPosition(v, getAdapterPosition());
        }

        /**
         * This pattern for handling RecyclerView clicks from
         * http://stackoverflow.com/a/24933117/967131
         */
        public interface ViewHolderOnClickListener {
            void onClickItemPosition(View v, int position);
        }
    }

    public PersonAdapter(List<Person> people) {
        mItems = people;
        Collections.sort(mItems, new Comparator<Person>() {
            @Override
            public int compare(Person lhs, Person rhs) {
                try {
                    Date lDate = DateUtils.getDateFormat().parse(lhs.getBirthDate());
                    Date rDate = DateUtils.getDateFormat().parse(rhs.getBirthDate());
                    return lDate.compareTo(rDate);
                } catch (ParseException pe) {
                    // Fail fast - there should never be incorrectly formatted dates in the database
                    // if there are, we need to know about it
                    throw new RuntimeException("Invalid dates--cannot compare " + lhs.getBirthDate()
                            + " and " + rhs.getBirthDate(), pe);
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_person, parent, false);
        return new ViewHolder(v, new ViewHolder.ViewHolderOnClickListener() {
            @Override
            public void onClickItemPosition(View v, int position) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CreateEditPersonActivity.class);
                intent.putExtra(CreateEditPersonActivity.EXTRA_PERSON, mItems.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Person person = mItems.get(position);

        holder.mNameTextView.setText(person.getFirstName() + " " + person.getLastName());
        holder.mZipCodeTextView.setText(person.getZipCode());
        holder.mBirthDateTextView.setText(person.getBirthDate());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
