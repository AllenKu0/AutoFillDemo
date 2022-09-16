package com.example.autofillex.service;

import android.app.PendingIntent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.CancellationSignal;

import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.util.Log;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.autofillex.AddAccount.AddAccountActivity;
import com.example.autofillex.DataBase.AccountDataBase;
import com.example.autofillex.DataBase.AccountEntity;
import com.example.autofillex.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyAutofillService extends AutofillService {


    private static Context context;
    public static final String HINT_TYPE_NAME = "name";
    public static final String HINT_TYPE_PASSWORD = "password";
    public static final String HINT_TYPE_PHONE = "phone";
    public static final String HINT_TYPE_PACKAGE = "package";
    //全部HintType
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE = "creditCardExpirationDate";
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY = "creditCardExpirationDay";
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH = "creditCardExpirationMonth";
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR = "creditCardExpirationYear";
    public static final String AUTOFILL_HINT_CREDIT_CARD_NUMBER = "creditCardNumber";
    public static final String AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE = "creditCardSecurityCode";
    public static final String AUTOFILL_HINT_EMAIL_ADDRESS = "emailAddress";
    public static final String AUTOFILL_HINT_POSTAL_ADDRESS = "postalAddress";
    public static final String AUTOFILL_HINT_POSTAL_CODE = "postalCode";
    public static final String AUTOFILL_HINT_USERNAME = "username";

    public static final List<String> HINT_TYPE_COLLECTIONS = new ArrayList();

    private static List<AccountEntity> accountList = new ArrayList<>();

    static {
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_NAME);
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_PASSWORD);
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_PHONE);
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_PACKAGE);
        HINT_TYPE_COLLECTIONS.add(AUTOFILL_HINT_USERNAME);
    }


    //模拟数据库中的数据，实际应用中，应该将这个数据保存在数据库中
    private static final List<Map<String, String>> suggestions = new ArrayList();

    static {
        //模拟数据库中的数据，实际应用中，应当把表单保存到数据库中，填充时再从数据库读取
//        Map<String, String> suggestion1 = new HashMap();
//        suggestion1.put(HINT_TYPE_NAME, "表單1");
//        suggestion1.put(HINT_TYPE_PASSWORD, "123456");
//        suggestion1.put(HINT_TYPE_PHONE, "18420015500");
//        suggestions.add(suggestion1);
//        Map<String, String> suggestion2 = new HashMap();
//        suggestion2.put(HINT_TYPE_NAME, "表單2");
//        suggestion2.put(HINT_TYPE_PASSWORD, "abcdefg");
//        suggestion2.put(HINT_TYPE_PHONE, "17935842251");
//        suggestions.add(suggestion2);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
    }

    //自動填入值發生變更時，跳出
    //儲存新帳號時也會
    @RequiresApi(api = 33)
    @Override
    public void onSaveRequest(SaveRequest request, SaveCallback callback) {
        Log.e("TAG", "onSaveRequest: ");

        //获取所有自动填充的节点
        List<AssistStructure> structures = request.getFillContexts().stream().map(FillContext::getStructure).collect(Collectors.toList());
        List<AssistStructure.ViewNode> viewNodes = new ArrayList();
        parseAllAutofillNode(structures, viewNodes);
        //保存表单内容
        //每条建议记录对应一个Map，Map每个键值対代表控件的HintType和文本值
        JSONObject newAccount = new JSONObject();
        Map<String, String> suggestion = new HashMap();
        for (AssistStructure.ViewNode viewNode : viewNodes) {
            String hintType = viewNode.getAutofillHints()[0];
            String value = viewNode.getText().toString();
            suggestion.put(hintType, value);
            try {
                newAccount.put(hintType, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AccountEntity accountEntity = new Gson().fromJson(String.valueOf(newAccount), AccountEntity.class);
        insertAccount(accountEntity);
//        suggestions.add(suggestion);
        //成功
        callback.onSuccess();
    }

    //執行自動填入內容記錄要求
    @Override
    public void onFillRequest(FillRequest request, CancellationSignal cancellationSignal, FillCallback callback) {
        Log.e("TAG", "onFillRequest: ");
        // ---------------------------Room 版------------------------------------
        getAccountList(new CallBack() {
            @Override
            public void onSuccess() {
//                getFillResponse(request);
                callback.onSuccess(getFillResponse(request));
            }

            @Override
            public void onFail() {

            }
        });

        // ---------------------------HashMap 版------------------------------------

//        //获取所有自动填充节点的AutofillId和HintType(AssistStructure包含一切)
//        List<AssistStructure> structures = request.getFillContexts().stream().map(FillContext::getStructure).collect(Collectors.toList());
//        //AutofillId 識別自動填充節點的唯一值/String包含name，password，phone等所有保存的字段
//        Map<AutofillId, String> hintTypeMap = parseAllHintType(structures);
//        //每条建议记录对应填充服务中的一个Dataset对象
//        //每个Dataset代表了一套数据，包含name，password，phone等所有保存的字段
//        //我们用Map来记录Dataset的数据，从而可以方便得将其存储到数据库或内存中
//        FillResponse.Builder fillResponseBuilder = new FillResponse.Builder();
//        Intent activityIntent = new Intent(this, MainActivity.class);
////        activityIntent.setAction("android.service.autofill.AutofillService");
////                activityIntent.setAction("com.allen.AutoFillServerApp");//自訂的 action name
//        IntentSender intentSender = PendingIntent.getActivity(
//                this, 100, activityIntent,
//                PendingIntent.FLAG_CANCEL_CURRENT
//        ).getIntentSender();
//        //存幾個帳密
////        for (AccountEntity accountEntity : accountList) {
////            Dataset.Builder datasetBuilder = new Dataset.Builder();
////            //獲取已存的帳號
////            String name = accountEntity.getName();
////            //建立autoFill的view
////            RemoteViews presentation = createPresentation(name);
////
////            //存帳密
////            for (AutofillId autofillId : hintTypeMap.keySet()) {
////                String accountString = new Gson().toJson(accountEntity);
////                try {
////                    JSONObject accountJson = new JSONObject(accountString);
////
////                    //将suggestion中的单个字段加入dataset
////                    //獲得如HINT_TYPE_NAME、HINT_TYPE_PASSWORD
////                    String hintType = hintTypeMap.get(autofillId);
////                    //獲得對應HINT_TYPE_NAME、HINT_TYPE_PASSWORD的值
////                    String value = (String) accountJson.get(hintType);
////
////                    if (value != null)
////                        datasetBuilder.setValue(autofillId, AutofillValue.forText(value), presentation);
////                    //设置需要保存的表单节点，这一步一定要有，否则Activity退出时不会保存表单
////                    //存內容，值為hintType轉成數字和autofillId
////                    SaveInfo.Builder saveInfoBuilder = new SaveInfo.Builder(HINT_TYPE_COLLECTIONS.indexOf(hintType), new AutofillId[]{autofillId});
////                    //设置关联的节点，如果不设置，只有所有节点值发生变化时，系统才认为表单发生了变更，才会询问是否要保存表单
////                    //設置有意保存的AutofillId
////                    saveInfoBuilder.setOptionalIds(hintTypeMap.keySet().stream().toArray(AutofillId[]::new));
////                    SaveInfo saveInfo = saveInfoBuilder.build();
////                    fillResponseBuilder.setSaveInfo(saveInfo);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//////                fillResponseBuilder.setAuthentication(hintTypeMap.keySet().stream().toArray(AutofillId[]::new),intentSender,presentation);
////            }
////            Dataset dataset = datasetBuilder.build();
////            fillResponseBuilder.addDataset(dataset);
////        }
//
//        for (Map<String, String> suggestion : suggestions) {
//            Dataset.Builder datasetBuilder = new Dataset.Builder();
//            //獲取已存的帳號
//            String name = suggestion.get(HINT_TYPE_NAME);
////            String name = accountList.get(0).getName();
//            //建立autoFill的view
//            RemoteViews presentation = createPresentation(name);
//            //存帳密
//            for (AutofillId autofillId : hintTypeMap.keySet()) {
//                //将suggestion中的单个字段加入dataset
//                //獲得如HINT_TYPE_NAME、HINT_TYPE_PASSWORD
//                String hintType = hintTypeMap.get(autofillId);
//                //獲得對應HINT_TYPE_NAME、HINT_TYPE_PASSWORD的值
////                suggestion.get(hintType)
//                String value = suggestion.get(hintType);
//                if (value != null)
//                    datasetBuilder.setValue(autofillId, AutofillValue.forText(value), presentation);
//                //设置需要保存的表单节点，这一步一定要有，否则Activity退出时不会保存表单
//                //存內容，值為hintType轉成數字和autofillId
//                SaveInfo.Builder saveInfoBuilder = new SaveInfo.Builder(HINT_TYPE_COLLECTIONS.indexOf(hintType), new AutofillId[]{autofillId});
//                //设置关联的节点，如果不设置，只有所有节点值发生变化时，系统才认为表单发生了变更，才会询问是否要保存表单
//                //設置有意保存的AutofillId
//                saveInfoBuilder.setOptionalIds(hintTypeMap.keySet().stream().toArray(AutofillId[]::new));
//                SaveInfo saveInfo = saveInfoBuilder.build();
//                fillResponseBuilder.setSaveInfo(saveInfo);
////                fillResponseBuilder.setAuthentication(hintTypeMap.keySet().stream().toArray(AutofillId[]::new),intentSender,presentation);
//            }
//            Dataset dataset = datasetBuilder.build();
//            fillResponseBuilder.addDataset(dataset);
//        }
//        FillResponse fillResponse = fillResponseBuilder.build();
        //成功，並找到對應填充值
//        callback.onSuccess(fillResponse);
    }

    //取所有自動填充的節點
    private void parseAllAutofillNode(List<AssistStructure> structures, List<AssistStructure.ViewNode> autofillNodes) {
        for (AssistStructure structure : structures) {
            int windowNodeCount = structure.getWindowNodeCount();
            for (int i = 0; i < windowNodeCount; i++) {
                AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
                AssistStructure.ViewNode rootViewNode = windowNode.getRootViewNode();
                parseAllAutofillNode(rootViewNode, autofillNodes);
            }
        }
    }

    //獲取所有自動填充的節點
    private void parseAllAutofillNode(AssistStructure.ViewNode viewNode, List<AssistStructure.ViewNode> autofillNodes) {
        if (viewNode.getAutofillHints() != null)
            autofillNodes.add(viewNode);
        int childCount = viewNode.getChildCount();
        for (int i = 0; i < childCount; i++)
            parseAllAutofillNode(viewNode.getChildAt(i), autofillNodes);
    }

    //獲取所有Autofill節點的HintType
    private Map<AutofillId, String> parseAllHintType(List<AssistStructure> structures) {
        Map<AutofillId, String> hintTypeMap = new HashMap();
        for (AssistStructure structure : structures) {
            int windowNodeCount = structure.getWindowNodeCount();
            for (int i = 0; i < windowNodeCount; i++) {
                //取windowNode
                AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
                //取rootViewNode
                AssistStructure.ViewNode rootViewNode = windowNode.getRootViewNode();
                //從rootViewNode轉出hintType，並加入hintTypeMap
                parseAllHintType(rootViewNode, hintTypeMap);
            }
        }
        return hintTypeMap;
    }

    //获取所有Autofill节点的HintType
    private void parseAllHintType(AssistStructure.ViewNode viewNode, Map<AutofillId, String> hintTypeMap) {
        //getAutofillHints 獲取數據內容，如:password、account
        if (viewNode.getAutofillHints() != null)
            hintTypeMap.put(viewNode.getAutofillId(), viewNode.getAutofillHints()[0]);
        int childCount = viewNode.getChildCount();
        for (int i = 0; i < childCount; i++)
            parseAllHintType(viewNode.getChildAt(i), hintTypeMap);
    }

    //创建一个表单建议对应的View
    private RemoteViews createPresentation(String name) {
        RemoteViews presentation = new RemoteViews(getPackageName(), R.layout.item_autofill_value);
        presentation.setTextViewText(R.id.text, name);
        return presentation;
    }

    private void getAccountList(MyAutofillService.CallBack callBack) {
        AccountDataBase.getInstance(context).getDataDao().displayAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<AccountEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<AccountEntity> accountEntities) {
                        accountList = accountEntities;
                        callBack.onSuccess();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callBack.onFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void insertAccount(AccountEntity accountEntity) {
        AccountDataBase.getInstance(context).getDataDao().insertData(accountEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    private FillResponse getFillResponse(FillRequest request){
        //獲取所有自動填充的節點的AutofillId和HintType(AssistStructure包含一切)
        List<AssistStructure> structures = request.getFillContexts().stream().map(FillContext::getStructure).collect(Collectors.toList());
        //AutofillId 識別自動填充節點的唯一值/String包含name，password，phone等所有保存的字段
        Map<AutofillId, String> hintTypeMap = parseAllHintType(structures);
        //每個建議紀錄對應autoFill中的一個Dataset
        //每個Dataset代表了一套數據(如:帳號及密碼)
        FillResponse.Builder fillResponseBuilder = new FillResponse.Builder();
        Intent activityIntent = new Intent(this, AddAccountActivity.class);
//        activityIntent.setAction("android.service.autofill.AutofillService");
//                activityIntent.setAction("com.allen.AutoFillServerApp");//自訂的 action name
        IntentSender intentSender = PendingIntent.getActivity(
                this, 100, activityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        ).getIntentSender();
        //存幾個帳密
        for (AccountEntity accountEntity : accountList) {
            //建立DataSet
            Dataset.Builder datasetBuilder = new Dataset.Builder();
            //獲取已存的帳號
            String name = accountEntity.getUsername();
            //建立autoFill的view
            RemoteViews presentation = createPresentation(name);
            //存帳密
            for (AutofillId autofillId : hintTypeMap.keySet()) {
                String accountString = new Gson().toJson(accountEntity);
                try {
                    JSONObject accountJson = new JSONObject(accountString);
                    //獲得如HINT_TYPE_NAME、HINT_TYPE_PASSWORD
                    String hintType = hintTypeMap.get(autofillId);
                    //獲得對應HINT_TYPE_NAME(name)、HINT_TYPE_PASSWORD(password)的值
                    String value = (String) accountJson.get(hintType);

                    if (value != null)
                        datasetBuilder.setValue(autofillId, AutofillValue.forText(value), presentation);
                    //設置需要保存的表單節點(autofillId)，這一步一定要有，否則Activity退出時不會保存表單
                    //存內容，值為hintType轉成數字(可自訂)和autofillId
                    SaveInfo.Builder saveInfoBuilder = new SaveInfo.Builder(HINT_TYPE_COLLECTIONS.indexOf(hintType), new AutofillId[]{autofillId});
                    //設置關聯節點，如果不設置，只有所有節點值發生變化時，系统才認為表單發生了变更，才會詢問是否要保存表單
                    //設置有意保存的AutofillId
                    saveInfoBuilder.setOptionalIds(hintTypeMap.keySet().stream().toArray(AutofillId[]::new));
                    SaveInfo saveInfo = saveInfoBuilder.build();
                    fillResponseBuilder.setSaveInfo(saveInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                fillResponseBuilder.setAuthentication(hintTypeMap.keySet().stream().toArray(AutofillId[]::new),intentSender,presentation);
            }
            Dataset dataset = datasetBuilder.build();
            fillResponseBuilder.addDataset(dataset);
        }

        FillResponse fillResponse = fillResponseBuilder.build();
        return fillResponse;
    }

    interface CallBack{
        void onSuccess();
        void onFail();
    }
}
