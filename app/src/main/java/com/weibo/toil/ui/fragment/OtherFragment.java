package com.weibo.toil.ui.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.R;
import com.weibo.toil.api.other.KudiApi;
import com.weibo.toil.bean.other.Kuaidi;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.view.CustomEdittext;
import com.weibo.toil.ui.view.StereoView;
import com.weibo.toil.utils.NetWorkUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OtherFragment extends BaseFragment {

    @BindView(R.id.tv_result)
    TextView tv_result;

    @BindView(R.id.stereoView)
    StereoView stereoView;

    @BindView(R.id.search_bianma)
    CustomEdittext search_bianma;

    @BindView(R.id.search_danhao)
    CustomEdittext search_danhao;

    private MainActivity mActivity;

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView(){
        stereoView.setCan3D(true);
        stereoView.setResistance(3f)
                .setInterpolator(new BounceInterpolator())
                .setStartScreen(1);

        tv_result.setText("查快递:快递公司编码:申通=”shentong” EMS=”ems” " +
                "顺丰=”shunfeng” 圆通=”yuantong” 中通=”zhongtong” " +
                "韵达=”yunda” 天天=”tiantian” 汇通=”huitongkuaidi” " +
                "全峰=”quanfengkuaidi” 德邦=”debangwuliu” " +
                "宅急送=”zhaijisong”\n");

    }

    @OnClick(R.id.search)
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.search:
                String danhao = search_danhao.getText().toString();
                String bianma = search_bianma.getText().toString();
                if (!TextUtils.isEmpty(danhao) && !TextUtils.isEmpty(bianma)) {
                    if (NetWorkUtil.isNetWorkAvailable(mActivity)) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Constant.KUAIDI)
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .build();
                        retrofit.create(KudiApi.class).searchKudi(bianma, danhao)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Kuaidi>() {
                                    @Override
                                    public void call(Kuaidi kuaidi) {
                                        tv_result.setText(kuaidi.getMessage());
                                    }
                                });
                    }
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other;
    }

    public OtherFragment() {}

    public static OtherFragment newInstance() {
        OtherFragment fragment = new OtherFragment();
        return fragment;
    }

}
