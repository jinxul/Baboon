package com.givekesh.baboon.fcm;

import com.givekesh.baboon.Utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        new Utils(this).sendRegistrationToServer(refreshedToken);
    }
}
