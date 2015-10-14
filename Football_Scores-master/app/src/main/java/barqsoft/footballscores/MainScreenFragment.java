package barqsoft.footballscores;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.sync.FootballSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    public scoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String _DATE = "_DATE";
    private String scoresDate;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    private LinearLayout error;
    private TextView errorMessage;

    public static MainScreenFragment newInstance(String date) {
        Bundle args = new Bundle();
        args.putString(_DATE, date);

        MainScreenFragment fragment = new MainScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MainScreenFragment() {
    }

    private void update_scores() {
        FootballSyncAdapter.syncImmediately(getActivity());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        scoresDate = getArguments().getString(_DATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.scores_list);
        error = (LinearLayout) view.findViewById(R.id.error_view);
        errorMessage = (TextView) view.findViewById(R.id.error_message);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mAdapter = new scoresAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.columns), StaggeredGridLayoutManager.VERTICAL));

        swipeRefreshLayout.setOnRefreshListener(this);
        sRefreshing(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.mToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        int topRowVerticalPosition =
                                (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ? 0 : mRecyclerView.getChildAt(0).getTop();
                        swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

                    }
                });
    }

    @Override
    public void onStop() {
        swipeRefreshLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        error.setVisibility(View.GONE);

        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{scoresDate}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mAdapter.swapCursor(cursor);
        if(cursor.getCount() > 0) {
            error.setVisibility(View.GONE);
        } else {
            errorMessage.setText(R.string.no_matches_for_day);
            error.setVisibility(View.VISIBLE);
        }
        sRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
        sRefreshing(false);

    }


    @Override
    public void onRefresh() {
        update_scores();
    }

    private void sRefreshing(final boolean refreshing) {
        //Log.v(mLogTag, "postRefreshing: refreshing=" + refre
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(refreshing);
                }
            });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FootballApplication.get(getActivity()).getRefWatcher().watch(this);
    }
}
