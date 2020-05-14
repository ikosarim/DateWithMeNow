package com.example.datingapplication.ui.date_map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.datingapplication.R;
import com.mapbox.geojson.Point;

import java.util.ArrayList;

import static java.lang.Double.parseDouble;

public class PersonCardAdapter extends ArrayAdapter<PersonCardViewModel> {

    private final Integer PHONE_CALL_CODE = 1;

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<PersonCardViewModel> personCardViewModelList;

    private Context context;

    PersonCardAdapter(Context context, int resource, ArrayList<PersonCardViewModel> personCardViewModelList) {
        super(context, resource, personCardViewModelList);
        this.context = context;
        this.personCardViewModelList = personCardViewModelList;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PersonCardViewModel personCardViewModel = personCardViewModelList.get(position);

        viewHolder.getNameView().setText(personCardViewModel.getName());
        viewHolder.getAgeView().setText(personCardViewModel.getAge());
        viewHolder.getCommentView().setText(personCardViewModel.getComment());

        viewHolder.getCallButton().setOnClickListener(v -> OnClickDoCall(personCardViewModel));
        viewHolder.getConfirmButton().setOnClickListener(v ->
                ((DateMapActivity) context).buildRouteToSelectedPerson(
                        Point.fromLngLat(
                                parseDouble(personCardViewModel.getLongitude()),
                                parseDouble(personCardViewModel.getLatitude())
                        )
                ));
        return convertView;
    }

    private void OnClickDoCall(PersonCardViewModel personCardViewModel) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w("", "Nedd to request permission");
            sendLocationPermissionRequest(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
        }
        String phoneNumber = personCardViewModel.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNumber, null));
        context.startActivity(callIntent);
    }

    private class ViewHolder {
        final Button callButton, confirmButton;
        final TextView nameView, ageView, commentView;

        ViewHolder(View view) {
            callButton = (Button) view.findViewById(R.id.call_button);
            confirmButton = (Button) view.findViewById(R.id.confirm_button);
            nameView = (TextView) view.findViewById(R.id.name_view);
            ageView = (TextView) view.findViewById(R.id.age_view);
            commentView = (TextView) view.findViewById(R.id.comment_view);
        }

        public Button getCallButton() {
            return callButton;
        }

        public Button getConfirmButton() {
            return confirmButton;
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getAgeView() {
            return ageView;
        }

        public TextView getCommentView() {
            return commentView;
        }
    }

    private void sendLocationPermissionRequest(String permission, int code) {
        ActivityCompat.requestPermissions(
                (Activity) getContext(),
                new String[]{permission},
                code
        );
    }
}
