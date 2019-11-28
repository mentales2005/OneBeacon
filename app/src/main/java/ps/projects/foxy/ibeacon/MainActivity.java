package ps.projects.foxy.ibeacon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import io.onebeacon.api.OneBeacon;
import io.onebeacon.api.ScanStrategy;

import static android.content.Context.BIND_AUTO_CREATE;


public class MainActivity extends Activity implements ServiceConnection {
    private MonitorService mService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }else{
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show();
            if (!bindService(new Intent(this, MonitorService.class), this, BIND_AUTO_CREATE)) {
                setTitle("Bind failed! Manifest?");

            }

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Do you work
                    if (!bindService(new Intent(this, MonitorService.class), this, BIND_AUTO_CREATE)) {
                        setTitle("Bind failed! Manifest?");

                    }
                } else {
                    Toast.makeText(this, "Can not proceed! i need permission" , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static boolean isPermissionGranted(@NonNull String[] grantPermissions, @NonNull int[] grantResults,
                                              @NonNull String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((MonitorService.LocalServiceBinder) service).getService();
        setTitle("Service connected");
        // make the service to stick around by actually starting it
        startService(new Intent(this, MonitorService.class));

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
        setTitle("Service disconnected");

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity is visible, scan with most reliable results
        OneBeacon.setScanStrategy(ScanStrategy.LOW_LATENCY);
    }

    @Override
    protected void onPause() {
        // Activity is not in foreground, make a trade-off between battery usage and scan latency
        OneBeacon.setScanStrategy(ScanStrategy.BALANCED);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Activity is gone, set scan mode to use lowest possible power usage
        OneBeacon.setScanStrategy(ScanStrategy.LOW_POWER);
        if (null != mService) {
            // optionally stop the service if running in background is not desired
//            stopService(new Intent(this, MonitorService.class));
            unbindService(this);
            mService = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBindingDied(ComponentName name) {

    }

    @Override
    public void onNullBinding(ComponentName name) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
