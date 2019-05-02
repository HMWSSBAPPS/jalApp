package com.hmwssb.jalapp;

import java.util.Vector;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Report extends Fragment {

    Vector<String> dataVec = new Vector<String>(),

    line_vec = new Vector<String>(), line_dataVe = new Vector<String>(),
            valve_Vec = new Vector<String>();

    LinearLayout ll_report_main;

    LinearLayout view_headder_1[];
    Button btn_map[];
    View[] v_main;
    Dialog prog;
    int[] view_count;
    TextView tv_exapnd[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.report, container, false);

        ll_report_main = (LinearLayout) view.findViewById(R.id.ll_report_main);

        new LoadData().execute();

        return view;
    }

    public void showProgressDialog() {

        try {
            prog = new Dialog(getActivity(), R.style.AppTheme1);
            prog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            prog.setContentView(R.layout.progress_layout);
            prog.setCancelable(false);
            prog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            showProgressDialog();

        }

        @Override
        protected Void doInBackground(Void... params) {

            DBHelper dbh = new DBHelper(getActivity());
            SQLiteDatabase db = dbh.getWritableDatabase();
            String Query = "SELECT * FROM " + DBHelper.TABLE_VALVE + " WHERE "
                    + DBHelper.VALVE_SUB_STATUS + "='1'";
            Cursor c = db.rawQuery(Query, null);
            dataVec.removeAllElements();
            line_vec.removeAllElements();
            line_dataVe.removeAllElements();
            valve_Vec.removeAllElements();
            while (c.moveToNext()) {
                dataVec.addElement(c.getString(c
                        .getColumnIndex(DBHelper.VLAVE_LINEID))
                        + "^"

                        + c.getString(c.getColumnIndex(DBHelper.VALVE_LANDMARK))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_AREA))
                        + "^"
                        + c.getString(c
                        .getColumnIndex(DBHelper.VALVE_VALVETYPE))
                        + "^"

                        + c.getString(c.getColumnIndex(DBHelper.VALVE_STATUS))
                        + "^"

                        + c.getString(c.getColumnIndex(DBHelper.VALVE_LATITUDE))
                        + "^"

                        + c.getString(c
                        .getColumnIndex(DBHelper.VALVE_LONGITUDE)) + "^"

                        + c.getString(c
                        .getColumnIndex(DBHelper.VALVE_SUPLY_AREA)));
            }
            c.close();
            db.close();
            System.out.println("dataVEc......" + dataVec);
            for (int i = 0; i < dataVec.size(); i++) {

                String split[] = dataVec.elementAt(i).toString().split("\\^");

                if (!line_vec.contains(split[0])) {
                    line_vec.addElement(split[0]);
                    line_dataVe.addElement(split[7] + "(" + split[0] + ")");
                    valve_Vec.addElement(split[1] + "^" + split[3] + "^"
                            + split[4] + "^" + split[5] + "^" + split[6]);
                } else {
                    valve_Vec.setElementAt(
                            valve_Vec.elementAt(line_vec.indexOf(split[0]))
                                    + "@" + split[1] + "^" + split[3] + "^"
                                    + split[4] + "^" + split[5] + "^"
                                    + split[6], line_vec.indexOf(split[0]));

                }

            }

            System.out.println("line vec............." + line_vec);
            System.out.println("line data vec............." + line_dataVe);
            System.out.println("valve vec............." + valve_Vec);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (line_vec.isEmpty()) {
                Helper.showShortToast(getActivity(), "No Valves to report...");
                prog.dismiss();
            } else {
                CreateDynamicLayout();
            }

        }

    }

    private View addVerticalSpace() {
        View v = new View(getActivity());
        LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, 3);
        v.setLayoutParams(layoutParams);
        v.setBackgroundColor(Color.parseColor("#995A5F"));
        return v;
    }

    private View addVerticalSpace1() {
        View v = new View(getActivity());
        LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, 3);
        v.setLayoutParams(layoutParams);
        return v;
    }

    public void CreateDynamicLayout() {
        ll_report_main.removeAllViews();
        if (!line_vec.isEmpty()) {
            view_count = new int[line_vec.size()];
            v_main = new View[line_vec.size()];
            view_headder_1 = new LinearLayout[line_vec.size()];
            btn_map = new Button[line_vec.size()];
            tv_exapnd = new TextView[line_vec.size()];
            for (int i = 0; i < line_vec.size(); i++) {
                final int pos = i;
                view_count[i] = 0;
                v_main[i] = getActivity().getLayoutInflater().inflate(
                        R.layout.report_header_adapter, null);
                TextView tv_lineman_name = (TextView) v_main[i]
                        .findViewById(R.id.tv_lineman_name);
                tv_exapnd[i] = (TextView) v_main[i]
                        .findViewById(R.id.tv_personal_exapand);
                tv_lineman_name.setText(line_dataVe.elementAt(i).toString());
                btn_map[i] = (Button) v_main[i].findViewById(R.id.btn_show_map);
                btn_map[i].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), MapActivity.class);
                        i.putExtra("DATA", valve_Vec.elementAt(pos).toString());
                        startActivity(i);

                    }
                });
                String split[] = valve_Vec.elementAt(i).toString().split("\\@");
                view_headder_1[i] = new LinearLayout(getActivity());
                view_headder_1[i].setOrientation(1);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                view_headder_1[i].setLayoutParams(lp);
                int main_count = 0, main_tota_count = 0, ope_count = 0, oper_tot_count = 0;
                for (int j = 0; j < split.length; j++) {
                    String split1[] = split[j].split("\\^");

                    View v1 = getActivity().getLayoutInflater().inflate(
                            R.layout.report_header_1_adapter, null);

                    TextView tv_valve_name = (TextView) v1
                            .findViewById(R.id.tv_valve_name);
                    tv_valve_name.setText((j + 1) + ". " + split1[0] + "("
                            + split1[1] + ")");
                    if (split1[1].trim().equalsIgnoreCase("main")) {
                        main_tota_count++;
                    }
                    if (split1[1].trim().equalsIgnoreCase("operating")) {
                        oper_tot_count++;
                    }

                    if (split1[2].trim().equalsIgnoreCase("1")) {
                        main_count++;
                    }
                    if (split1[1].trim().equalsIgnoreCase("operating")
                            && split1[2].trim().equalsIgnoreCase("2")) {
                        ope_count++;
                    }
                    ImageView img = (ImageView) v1
                            .findViewById(R.id.report_img);
                    if (split1[2].trim().equalsIgnoreCase("0")) {
                        img.setImageResource(R.drawable.valve_close);
                    } else if (split1[2].trim().equalsIgnoreCase("1")) {
                        img.setImageResource(R.drawable.valve_open);
                    } else if (split1[2].trim().equalsIgnoreCase("2")) {
                        img.setImageResource(R.drawable.valve_open_yellow);
                    }
                    view_headder_1[i].addView(v1);
                    view_headder_1[i].addView(addVerticalSpace());

                }

                View v1 = getActivity().getLayoutInflater().inflate(
                        R.layout.report_header_2_adapter, null);
                TextView tv_main_valve_count = (TextView) v1
                        .findViewById(R.id.tv_main_valve_count);
                tv_main_valve_count.setText(""
                        + (main_tota_count + oper_tot_count));
                TextView tv_oper_valve_count = (TextView) v1
                        .findViewById(R.id.tv_oper_valve_count);
                tv_oper_valve_count.setText("" + ope_count);
                TextView tv_open_count = (TextView) v1
                        .findViewById(R.id.tv_open_count);
                tv_open_count.setText("" + main_count);
                TextView tv_pending_count = (TextView) v1
                        .findViewById(R.id.tv_pending_count);
                tv_pending_count
                        .setText(""
                                + ((main_tota_count + oper_tot_count) - (main_count + ope_count)));
                view_headder_1[i].addView(v1);
                view_headder_1[i].setVisibility(View.GONE);
                ll_report_main.addView(v_main[i]);
                ll_report_main.addView(view_headder_1[i]);
                ll_report_main.addView(addVerticalSpace1());
                v_main[i].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (view_count[pos] == 0) {
                            v_main[pos]
                                    .setBackgroundResource(R.drawable.layout_corners);
                            view_headder_1[pos].setVisibility(View.VISIBLE);
                            view_count[pos] = 1;
                            tv_exapnd[pos].setText("-");
                        } else if (view_count[pos] == 1) {
                            v_main[pos]
                                    .setBackgroundResource(R.drawable.layout_corners_1);
                            view_headder_1[pos].setVisibility(View.GONE);
                            view_count[pos] = 0;
                            tv_exapnd[pos].setText("+");
                        }

                    }
                });
            }
        }
        prog.dismiss();
    }
}
