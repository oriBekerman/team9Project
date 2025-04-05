package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class BranchListManager {

    private static BranchListManager instance = null;

    private List<Branch> branches = null;
    private Runnable pendingCallback = null;

    private boolean initialized = false;
    private boolean requestSent = false;
    private final Object lock = new Object();

    public List<Branch> branchList = null;



    private BranchListManager() {
        System.out.println("✅ [BranchListManager] Created");

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
            System.out.println("✅ [BranchListManager] Registered to EventBus");
        } else {
            System.out.println("⚠️ [BranchListManager] Already registered to EventBus");
        }
    }

    public static synchronized BranchListManager getInstance() {
        if (instance == null) {
            instance = new BranchListManager();
        }
        return instance;
    }

    public void requestBranchList(Runnable callback) {
        System.out.println("🟡 [BranchListManager] requestBranchList called");

        synchronized (lock) {
            if (initialized) {
                System.out.println("✅ [BranchListManager] Already initialized → firing callback now");
                Platform.runLater(callback);
                return;
            }

            // Not initialized yet, save the callback and send request
            System.out.println("📌 [BranchListManager] Waiting for branch list → storing callback");
            this.pendingCallback = callback;

            if (!requestSent) {
                System.out.println("📡 [BranchListManager] Sending request to server...");
                SimpleClient.getClient().getBranchList();
                requestSent = true;
            }
        }
    }

//    @Subscribe
//    public void onBranchListSentEvent(BranchListSentEvent event) {
//        System.out.println("📬 [BranchListManager] onBranchListSentEvent triggered with " + event.branches.size() + " branches");
//
//        synchronized (lock) {
//            this.branches = event.branches;
//            this.initialized = true;
//
//            if (pendingCallback != null) {
//                Runnable toRun = pendingCallback;
//                pendingCallback = null;
//
//                System.out.println("🎯 [BranchListManager] Firing stored callback");
//                Platform.runLater(toRun);
//            } else {
//                System.out.println("⚠️ [BranchListManager] No pending callback to fire");
//            }
//        }
//    }

    @Subscribe
    public void onBranchListSentEvent(BranchListSentEvent event)
    {
        this.branchList = event.branches;
    }




//    @Subscribe
//    public void onBranchListSentEvent(BranchListSentEvent event) {
//        System.out.println("📬 [BranchListManager] Received branch list (" + event.branches.size() + ")");
//
//        this.branches = event.branches;
//
//        if (pendingCallback != null) {
//            System.out.println("🎯 [BranchListManager] Executing pending callback...");
//            Runnable action = pendingCallback;
//            pendingCallback = null;
//            Platform.runLater(action);  // Ensure it runs on the UI thread
//        } else {
//            System.out.println("⚠️ [BranchListManager] No pending callback");
//        }
//    }



//    public List<Branch> getBranches() {
//        return branches;
//    }

    public void reset() {
        synchronized (lock) {
            branches = null;
            initialized = false;
            requestSent = false;
            pendingCallback = null;
            System.out.println("🔄 [BranchListManager] Reset state");
        }
    }
}
