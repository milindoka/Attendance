package in.org.ilugbom.attendance;

import android.app.Dialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import android.view.Window;
import android.webkit.WebView;

public class HelpDialog
{
    WebView webView;
    public String fileName = "Help.html";
    public void showDialog(final Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.help);

        webView = (WebView) dialog.findViewById(R.id.simpleWebView);
        // displaying content in WebView from html file that stored in assets folder
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/" + fileName);

        dialog.show();
    }
}