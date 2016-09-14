package tellh.com.nolistadapter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import tellh.com.nolistadapter.bean.Response;
import tellh.com.nolistadapter.bean.User;
import tellh.com.nolistadapter.viewbinder.ControlerViewBinder;
import tellh.com.nolistadapter.viewbinder.ErrorBinder;
import tellh.com.nolistadapter.viewbinder.FooterBinder;
import tellh.com.nolistadapter.viewbinder.HeaderBinder;
import tellh.com.nolistadapter.viewbinder.ImageItemViewBinder;
import tellh.com.nolistadapter.viewbinder.ImageItemViewBinder.ImageItem;
import tellh.com.nolistadapter.viewbinder.LoadMoreFooterBinder;
import tellh.com.nolistadapter.viewbinder.UserViewBinder;
import tellh.nolistadapter_rv.adapter.FooterLoadMoreAdapterWrapper;
import tellh.nolistadapter_rv.adapter.RecyclerViewAdapter;
import tellh.nolistadapter_rv.viewbinder.utils.EasyEmptyViewBinder;
import tellh.nolistadapter_rv.viewbinder.provider.ViewBinderProvider;

import static tellh.nolistadapter_rv.adapter.FooterLoadMoreAdapterWrapper.UpdateType.LOAD_MORE;
import static tellh.nolistadapter_rv.adapter.FooterLoadMoreAdapterWrapper.UpdateType.REFRESH;

public class MainActivity extends AppCompatActivity implements FooterLoadMoreAdapterWrapper.OnReachFooterListener, ErrorBinder.OnReLoadCallback {

    private RecyclerView list;
    private RecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        Gson gson = new Gson();
        Response response = gson.fromJson(Response.responseJsonPage1, Response.class);
        List<User> userList = response.getItems();
        List<ViewBinderProvider> displayList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            displayList.add(new ImageItem(userList.get(i).getAvatar_url()));
            displayList.add(userList.get(i));
        }
        adapter = RecyclerViewAdapter.builder()
                .displayList(displayList)
                .addItemType(new UserViewBinder()) //different item type have different ways to bind data to ViewHolder.
                .addItemType(new ImageItemViewBinder())
                .addHeader(new ControlerViewBinder(this))
                .addHeader(new HeaderBinder("I am the first header! 我是沙发"))
                .addHeader(new HeaderBinder("I am the second header! 我是板凳"))
                .addFooter(new FooterBinder("------I am the footer!------"))
                .addFooter(new FooterBinder("------我是有底线的！-------"))
                .setLoadMoreFooter(new LoadMoreFooterBinder(), list, this)
                .setEmptyView(new EasyEmptyViewBinder(R.layout.empty_view))
//                .setErrorView(new EasyErrorViewBinder(R.layout.error_view))
                .setErrorView(new ErrorBinder(this))
                .build();
        list.setAdapter(adapter);
    }

    private void initView() {
        list = (RecyclerView) findViewById(R.id.list);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        list.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        adapter.clearViewBinderCache();
        super.onDestroy();
    }

    public void refresh() {
        refreshLayout.setRefreshing(true);
        Gson gson = new Gson();
        Response response = gson.fromJson(Response.responseJsonPage1, Response.class);
        List<User> userList = response.getItems();
        List<ViewBinderProvider> displayList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            displayList.add(new ImageItem(userList.get(i).getAvatar_url()));
            displayList.add(userList.get(i));
        }
        FooterLoadMoreAdapterWrapper footerLoadMoreAdapterWrapper = (FooterLoadMoreAdapterWrapper) adapter;
        footerLoadMoreAdapterWrapper.OnGetData(displayList, REFRESH);
        footerLoadMoreAdapterWrapper.hideErrorView(list);
        refreshLayout.setRefreshing(false);
    }

    public void loadMore() {
        FooterLoadMoreAdapterWrapper footerLoadMoreAdapterWrapper = (FooterLoadMoreAdapterWrapper) adapter;
        Gson gson = new Gson();
        Response response = gson.fromJson(Response.responseJsonPage2, Response.class);
        List<User> userList = response.getItems();
        List<ViewBinderProvider> displayList = new ArrayList<>();
        if (footerLoadMoreAdapterWrapper.getCurPage() != 3) {
            for (int i = 0; i < userList.size(); i++) {
                displayList.add(new ImageItem(userList.get(i).getAvatar_url()));
                displayList.add(userList.get(i));
            }
        }
        footerLoadMoreAdapterWrapper.OnGetData(displayList, LOAD_MORE);
    }

    @Override
    public void onToLoadMore(int curPage) {
        Toast.makeText(MainActivity.this, "on to load more!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMore();
            }
        }, 800);
    }

    public void clear() {
        adapter.clear(list);
    }

    public void showError() {
        adapter.showErrorView(list);
    }

    @Override
    public void reload() {
        refresh();
    }
}
