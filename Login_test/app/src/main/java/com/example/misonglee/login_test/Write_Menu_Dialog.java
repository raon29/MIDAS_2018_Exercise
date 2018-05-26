package com.example.misonglee.login_test;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.example.misonglee.login_test.pMainActivity.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Write_Menu_Dialog extends Dialog implements View.OnClickListener {

    private Context context;
    private String userID;
    private String session;

    private EditText write_title;
    private EditText write_content;
    private EditText write_price;

    private String string_title;
    private String string_content;
    private String string_price;
    private String string_menuId;

    public Write_Menu_Dialog(@NonNull Context _context, String _userID, String _session) {
        super(_context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        context = _context;
        userID = _userID;
        session = _session;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Write_Menu_Dialog", "onCreate - execute");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_menu_dialog);

        write_title = (EditText) findViewById(R.id.write_title);
        write_content = (EditText) findViewById(R.id.write_content);
        write_price = (EditText) findViewById(R.id.write_price);

        ImageButton write_cancle = (ImageButton)findViewById(R.id.write_cancle);
        ImageButton modify = (ImageButton)findViewById(R.id.modify);
        ImageButton delete = (ImageButton)findViewById(R.id.delete);
        ImageButton write_upload = (ImageButton)findViewById(R.id.write_upload);
        ImageButton btnGallery = (ImageButton)findViewById(R.id.btnGallery);

        write_cancle.setOnClickListener(this);
        modify.setOnClickListener(this);
        delete.setOnClickListener(this);
        write_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.write_cancle:
                cancel();
                break;
            case R.id.write_upload:
                WriteDB writeDB = new WriteDB();
                writeDB.execute();
                break;
            case R.id.modify:
                ModifyDB modifyDB = new ModifyDB();
                modifyDB.execute();
                break;
            case R.id.delete:
                DeleteDB deleteDB = new DeleteDB();
                deleteDB.execute();
                break;
            case R.id.btnGallery:
                //갤러리에서 사진가져오기
                break;
        }
    }

    //글 정보 세팅
    public void setContent(String title, String content, String price, String menuID){
        //제목, 내용 세팅
        string_title = title;
        string_content = content;
        string_price = price;
        string_menuId = menuID;
    }

    class WriteDB extends AsyncTask<String, Void, String> {

        String target;

        // 전송할 데이터 및 서버의 URL을 사전에 정의합니다.
        @Override
        protected void onPreExecute() {

            string_title = write_title.getText().toString();
            string_content = write_content.getText().toString();
            string_price = write_price.getText().toString();

            try {
                target = MainActivity.URL + "menuAdd.midas?userID=" + URLEncoder.encode(userID, "UTF-8") + "&session=" + URLEncoder.encode(session, "UTF-8") + "&menuTitle="+ URLEncoder.encode(string_title, "UTF-8") + "&menuInformation=" + URLEncoder.encode(string_content, "UTF-8") + "&menuPrice="+ URLEncoder.encode(string_price,"UTF-8");
                Log.d("WriteDB", target);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("WriteDB", "doInBackground - execute");
            // 특정 URL로 데이터를 전송한 이후에 결과를 받아옵니다.
            try{
                // URL로 데이터를 전송합니다.
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                // 반환된 문자열을 읽어들입니다.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // 읽어들인 문자열을 반환합니다.
                return stringBuilder.toString().trim();
            } catch (Exception e){
                Log.e("WriteDB", "Exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        // 문자열을 JSON 형태로 처리합니다.
        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String verify = jsonObject.getString("verify");

                switch (verify){
                    case "1":
                        //글 작성 성공
                        successAlert("글 작성 성공", "글 작성이 정상적으로 완료되었습니다.");
                        break;
                    case "-1":
                        //권한 없음
                        failAlert("글 작성 실패", "글 작성할 권한이 없습니다.");
                        break;
                    default:
                        failAlert("글 작성 실패","글 작성에 실패했습니다.");
                        //글 작성 실패
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }



    class ModifyDB extends AsyncTask<String, Void, String> {
        String target;

        // 전송할 데이터 및 서버의 URL을 사전에 정의합니다.
        @Override
        protected void onPreExecute() {

            string_title = write_title.getText().toString();
            string_content = write_content.getText().toString();

            try {
                target = MainActivity.URL + "menuUpdate.midas?userID=" + URLEncoder.encode(userID, "UTF-8") + "&session=" + URLEncoder.encode(session, "UTF-8") + "&menuID="+ URLEncoder.encode( string_menuId, "UTF-8") + "&menuTitle=" + URLEncoder.encode(string_title, "UTF-8") + "&menuInformation=" + URLEncoder.encode(string_content, "UTF-8") + "&menuPrice="+ URLEncoder.encode(string_price, "UTF-8");
                Log.d("ModifyDB", target);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.d("ModifyDB", "doInBackground - execute");

            // 특정 URL로 데이터를 전송한 이후에 결과를 받아옵니다.
            try{
                // URL로 데이터를 전송합니다.
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                // 반환된 문자열을 읽어들입니다.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // 읽어들인 문자열을 반환합니다.
                return stringBuilder.toString().trim();
            } catch (Exception e){
                Log.e("WriteDB", "Exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        // 문자열을 JSON 형태로 처리합니다.
        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String verify = jsonObject.getString("verify");

                switch (verify){
                    case "1":
                        //글 작성 성공
                        successAlert("메뉴 수정 성공", "메뉴 수정이 정상적으로 완료되었습니다.");
                        break;
                    case "-1":
                        //권한 없음
                        failAlert("메뉴 수정 실패", "메뉴를 수정할 권한이 없습니다.");
                        break;
                    default:
                        failAlert("메뉴 수정 실패","메뉴 수정에 실패했습니다.");
                        //글 작성 실패
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DeleteDB extends AsyncTask<String, Void, String> {
        String target;

        // 전송할 데이터 및 서버의 URL을 사전에 정의합니다.
        @Override
        protected void onPreExecute() {

            try {
                target = MainActivity.URL + "menuDelete.midas?userID=" + URLEncoder.encode(userID, "UTF-8") + "&session=" + URLEncoder.encode(session, "UTF-8") + "&menuID=" + URLEncoder.encode(string_menuId, "UTF-8");
                Log.d("DeleteDB", target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("DeleteDB", "doInBackground - execute");

            // 특정 URL로 데이터를 전송한 이후에 결과를 받아옵니다.
            try{
                // URL로 데이터를 전송합니다.
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                // 반환된 문자열을 읽어들입니다.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // 읽어들인 문자열을 반환합니다.
                return stringBuilder.toString().trim();
            } catch (Exception e){
                Log.e("WriteDB", "Exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        // 문자열을 JSON 형태로 처리합니다.
        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String verify = jsonObject.getString("verify");

                switch (verify){
                    case "1":
                        //글 삭제 성공
                        successAlert("메뉴 삭제 성공", "메뉴 작성이 정상적으로 완료되었습니다.");
                        break;
                    case "-1":
                        //권한 없음
                        failAlert("메뉴 삭제 실패", "메뉴를 삭제할 권한이 없습니다.");
                        break;
                    default:
                        failAlert("메뉴 삭제 실패","이미 없는 메뉴입니다.");
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }


    //Log 만들기?


    public void successAlert(String title, String message){
        Log.d("Write_Content_Dialog","successAlert execute");

        //회원가입 dialog
        new AwesomeSuccessDialog(context)
                .setTitle(title)
                .setMessage(message)
                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_success, R.color.white)
                .setCancelable(true)
                .setPositiveButtonText("확인")
                .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        cancel();
                    }
                })
                .show();
    }

    public void failAlert(String title, String message) {
        Log.d("Write_Content_Dialog","failAlert Alert");

        //dialog
        new AwesomeWarningDialog(context)
                .setTitle(title)
                .setMessage(message)
                .setColoredCircle(R.color.dialogWarningBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.black)
                .setCancelable(true)
                .setButtonText("확인")
                .setButtonBackgroundColor(R.color.dialogWarningBackgroundColor)
                .setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        cancel();
                    }
                })
                .show();
    }



}
