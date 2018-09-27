package com.kash.kashsoft;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kash.kashsoft.appshortcuts.DynamicShortcutManager;


public class App extends Application {
    public static final String TAG = App.class.getSimpleName();

    public static final String GOOGLE_PLAY_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1lSvmM7/EYUlTk/G5v+dPZ3g5xIoifOALWdQZBnHPGG54IdJEMIwHm1XFShCY8cgOkxr//KAK63azCj9am+n4T4/5fVKlpm4GRazSasiecTDj5VMSS2ETdGC6Z8vFxOeBDdMryI3bsik6fwyYhmVkoVmK5ZazukY4Q2WtFozxDO2h+iDgml8G/jPv7844sCTnWSyP4hwUMFi+xbgPwsChUlGsiQJ5q9Fob++Sjidds9Qn18ZC3ENWHaKGvKY8CSikAjWj4mCjmj5d6UagMkPnZSEvdKr4K2sgdvUd/PfwfAbvOm071EJ0nHU6d/MJs1IxRoDnerPTrfoGoIgheFikwIDAQAB";
   public static final String PRO_VERSION_PRODUCT_ID = "pro_version";

    private static App app;

  private BillingProcessor billingProcessor;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        // default theme
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this)
                    .primaryColorRes(R.color.aqua)
                    .accentColorRes(R.color.md_pink_A400)
                    .commit();
        }

        // Set up dynamic shortcuts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).initDynamicShortcuts();
        }

       // automatically restores purchases
        billingProcessor = new BillingProcessor(this, App.GOOGLE_PLAY_LICENSE_KEY, new BillingProcessor.IBillingHandler() {
          @Override
          public void onProductPurchased(String productId, TransactionDetails details) {
          }

 @Override
  public void onPurchaseHistoryRestored() {
                Toast.makeText(App.this, R.string.restored_previous_purchase_please_restart, Toast.LENGTH_LONG).show();
  }

         @Override
        public void onBillingError(int errorCode, Throwable error) {
            }

            @Override
           public void onBillingInitialized() {
    }
 });
 }

 public static boolean isProVersion() {
   return BuildConfig.DEBUG || app.billingProcessor.isPurchased(PRO_VERSION_PRODUCT_ID);
 }

    public static App getInstance() {
        return app;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
      // billingProcessor.release();
    }
}
