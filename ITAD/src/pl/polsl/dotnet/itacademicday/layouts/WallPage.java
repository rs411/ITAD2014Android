package pl.polsl.dotnet.itacademicday.layouts;

import java.util.ArrayList;

import pl.polsl.dotnet.itacademicday.R;
import pl.polsl.dotnet.itacademicday.core.signalr.SignalRClient;
import pl.polsl.dotnet.itacademicday.core.signalr.SignalRClient.onMessageReceived;
import pl.polsl.dotnet.itacademicday.layouts.MainActivity.FontStyle;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class WallPage extends Page {

	public WallPage(Context c) {
		super(c);
	}

	private ListView mList;
	private WallAdapter mAdapter;

	private String mCommentAwaitForSubmit, mServerError;

	private SignalRClient mSignalerClient;

	private class WallAdapter extends BaseAdapter {
		private ArrayList<String> mMessages;

		public WallAdapter() {
			mMessages = new ArrayList<String>();
		}

		@Override
		public int getCount(){
			return mMessages.size();
		}

		@Override
		public String getItem(int position){
			return mMessages.get(position);
		}

		@Override
		public long getItemId(int position){
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			TextView t;
			if (convertView == null) {
				t = new TextView(getContext());
				t.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				MainActivity.setFont(t, FontStyle.SEMILIGHT);
				t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.descr_font));
				int p = (int) getResources().getDimension(R.dimen.wall_msg_padding);
				t.setPadding(p, p, p, p);
			} else {
				t = (TextView) convertView;
			}
			t.setText(getItem(position));
			return t;
		}

		private Runnable refresh = new Runnable() {

			@Override
			public void run(){
				mAdapter.notifyDataSetChanged();
			}
		};

		public void put(String message){
			mMessages.add(message);
			((MainActivity) getContext()).runOnUiThread(refresh);
		}
	}

	@Override
	protected void onCreate(){

		mCommentAwaitForSubmit = getResources().getString(R.string.await_for_comment_submit);
		mServerError = getResources().getString(R.string.server_error);
		final String connectionError = getResources().getString(R.string.connection_error);

		mList = (ListView) findViewById(R.id.wall_list);
		MainActivity.setFont(this, FontStyle.SEMILIGHT);
		MainActivity.setFont(findViewById(R.id.subtitle), FontStyle.LIGHT);
		MainActivity.tintView(findViewById(R.id.bottom_bar), Color.WHITE);

		mAdapter = new WallAdapter();
		mList.setAdapter(mAdapter);
		mSignalerClient = SignalRClient.getInstance();
		mSignalerClient.setReceiver(new onMessageReceived() {
			
			@Override
			public void onMessageReceive(String message) {
				mAdapter.put(message);
				
			}
		});
		
		for(String info:mSignalerClient.getWallInfo()){
			mAdapter.put(info);
		}

		final EditText commentEdit = (EditText) findViewById(R.id.commentEdit);
		final ImageButton commentSubmit = (ImageButton) findViewById(R.id.commentSubmit);
		commentEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
			}

			@Override
			public void afterTextChanged(Editable s){
				commentSubmit.setEnabled(s.length() > 2);
			}
		});
		commentSubmit.setEnabled(false);
		commentSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				if (MainActivity.hasAccessToNetwork()) {
					String t = commentEdit.getText().toString();
					if (onSubmit(t)) {
						commentEdit.setText("");
					}
				} else {
					Toast.makeText(getContext(), connectionError, Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/** Return true if success - message sent to server correctly*/
	public boolean onSubmit(String comment){
		boolean success;
		try {
			mSignalerClient.sendMessage(comment);
			success = true;
		} catch (Exception e) {
			success = false;
		}
		onRefresh();

		Toast.makeText(getContext(), success ? mCommentAwaitForSubmit : mServerError, Toast.LENGTH_LONG).show();
		return success;
	}

	/**Called after new messages were received. Scrolls the list to bottom*/
	public void onRefresh(){
		mList.post(new Runnable() {
			@Override
			public void run(){
				mList.setSelection(mAdapter.getCount() - 1);
			}
		});
	}

	@Override
	protected int getLayoutResourceId(){
		return R.layout.wall_fragment;
	}

	@Override
	public boolean onBack(){
		((MainActivity) getContext()).setContentPage(new AboutPage(getContext()));
		
		return true;
	}
}