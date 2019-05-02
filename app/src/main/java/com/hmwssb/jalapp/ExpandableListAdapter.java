package com.hmwssb.jalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<String>> _listDataChild;
	private HashMap<String, List<Integer>> _listchildImage;

	private HashMap<String, List<String>> valves_Images;
	private HashMap<String, List<String>> schedule;
	private HashMap<String, List<String>> time;
	private HashMap<String, List<String>> line_man;
	private HashMap<String, List<String>> line_id;
	private HashMap<String, List<String>> valve_id;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData,
			HashMap<String, List<Integer>> listChildImage,
			HashMap<String, List<String>> img,
			HashMap<String, List<String>> sch,
			HashMap<String, List<String>> tim,
			HashMap<String, List<String>> lineman,
			HashMap<String, List<String>> line,
			HashMap<String, List<String>> valveid) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this._listchildImage = listChildImage;
		this.valves_Images = img;
		this.schedule = sch;
		this.time = tim;
		this.line_man = lineman;
		this.line_id = line;
		this.valve_id = valveid;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_adapter, null);
		}

		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.tv_list_adapetr);
		txtListChild.setText(childText);

		ImageView img = (ImageView) convertView
				.findViewById(R.id.img_list_adapetr);
		ImageView img_valve = (ImageView) convertView
				.findViewById(R.id.img_valve);
		ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.pb_image);

		TextView tv_sch = (TextView) convertView
				.findViewById(R.id.tv_list_schedule);

		TextView tv_time = (TextView) convertView
				.findViewById(R.id.tv_list_status);

		String split[] = time.get(this._listDataHeader.get(groupPosition))
				.get(childPosition).split("\\-");

		if (split[0].trim().equals("0") && split[1].trim().equals("0")) {
			tv_time.setText("Status : --");
		} else if (!split[0].trim().equals("0") && split[1].trim().equals("0")) {
			tv_time.setText("Status : " + split[0] + "--");
		} else if (split[0].trim().equals("0") && !split[1].trim().equals("0")) {
			tv_time.setText("Status : " + "--" + split[1]);
		} else {
			tv_time.setText("Status : " + split[0] + "-" + split[1]);
		}

		tv_sch.setText("Schedule : "
				+ schedule.get(this._listDataHeader.get(groupPosition)).get(
						childPosition));
		if (valves_Images.get(this._listDataHeader.get(groupPosition))
				.get(childPosition).length() > 0
				&& !valves_Images.get(this._listDataHeader.get(groupPosition))
						.get(childPosition).equalsIgnoreCase("0")) {

			new GetImage(_context,
					valves_Images.get(this._listDataHeader.get(groupPosition))
							.get(childPosition), pb, img_valve, null, false,
					line_man.get(this._listDataHeader.get(groupPosition)).get(
							childPosition), line_id.get(
							this._listDataHeader.get(groupPosition)).get(
							childPosition), valve_id.get(
							this._listDataHeader.get(groupPosition)).get(
							childPosition));
			// pb.setVisibility(View.GONE);
			// img_valve.setVisibility(View.VISIBLE);
			// img_valve.setImageURI(Uri
			// .parse(valves_Images.get(
			// this._listDataHeader.get(groupPosition)).get(
			// childPosition)));

		} else {
			pb.setVisibility(View.GONE);
			img_valve.setVisibility(View.VISIBLE);

		}

		if (_listchildImage.get(this._listDataHeader.get(groupPosition)).get(
				childPosition) == 0) {
			img.setImageResource(R.drawable.valve_close);
		} else if (_listchildImage.get(this._listDataHeader.get(groupPosition))
				.get(childPosition) == 1) {
			img.setImageResource(R.drawable.valve_open);
		} else if (_listchildImage.get(this._listDataHeader.get(groupPosition))
				.get(childPosition) == 2) {
			img.setImageResource(R.drawable.valve_open_yellow);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_header_adapter,
					null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}