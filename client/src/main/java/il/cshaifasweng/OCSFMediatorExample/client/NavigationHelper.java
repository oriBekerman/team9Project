package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.App;
import il.cshaifasweng.OCSFMediatorExample.client.BranchPageBoundary;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class NavigationHelper {

    public static void openBranchPage(Branch branch, Runnable optionalCallback) {
        System.out.println("[NavigationHelper] Loading Branch.fxml...");
        try {
            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource("Branch.fxml"));
            Parent branchPageRoot = loader.load();
            System.out.println("✅ [NavigationHelper] FXML loaded");

            BranchPageBoundary controller = loader.getController();
            controller.setBranch(branch);
            System.out.println("📦 [NavigationHelper] Branch set on BranchPageBoundary");


            if (optionalCallback != null) {
                optionalCallback.run();
                System.out.println("[NavigationHelper] Callback run.");
            }

            System.out.println("📺 [NavigationHelper] Setting scene content...");
            App.setContent(branchPageRoot);  // ✅ CRITICAL LINE TO WATCH
            System.out.println("✅ [NavigationHelper] Scene content set");

        } catch (IOException e) {
            System.err.println("❌ [NavigationHelper] Failed to load Branch.fxml");
            e.printStackTrace();
        }
    }

}