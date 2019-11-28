package ps.projects.foxy.ibeacon;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import io.onebeacon.api.Beacon;
import io.onebeacon.api.BeaconsMonitor;
import io.onebeacon.api.Rangeable;
import io.onebeacon.api.spec.Apple_iBeacon;
import io.onebeacon.api.spec.EddystoneURIBeacon;

/** Example subclass for a BeaconsMonitor **/
class MyBeaconsMonitor extends BeaconsMonitor {
    Context mContext;
    public MyBeaconsMonitor(Context context) {
        super(context);
        this.mContext=context;
    }

    @Override
    protected void onBeaconChangedRange(Rangeable rangeable) {
        super.onBeaconChangedRange(rangeable);
        log(String.format("Range changed to %s for %s", rangeable.getRange(), rangeable));
    }

    @Override
    protected void onBeaconAdded(Beacon beacon) {
        super.onBeaconAdded(beacon);
        if (beacon.getType() == Beacon.Type.IBEACON) {
            // example usage for an iBeacon
            Apple_iBeacon iBeacon = (Apple_iBeacon) beacon;
            int maj = iBeacon.getMajor();
            int min = iBeacon.getMinor();
            UUID uuid = iBeacon.getUUID();

            log(String.format("{%s}/%d/%d new iBeacon found: %s", uuid, maj, min, beacon));
        }
         if(beacon.getType()==Beacon.Type.EDDYSTONE_URI){

             EddystoneURIBeacon EddyBeacon=(EddystoneURIBeacon) beacon;

            log(String.format(EddyBeacon.getName()+" "+EddyBeacon.getData()+" "+EddyBeacon.getId()+" "+EddyBeacon.getRangeName()+" "+EddyBeacon.getPrettyAddress()+" "+EddyBeacon.getMeasuredPower()));

        }

        // see Beacon.Type.* for more types, and io.onebeacon.api.spec.* for beacon type interfaces
    }

    // checkout the other available callbacks in the BeaconsManager base class

    private void log(String msg) {
        Log.d("MonitorService", msg);
    }
}