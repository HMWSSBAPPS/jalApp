package com.hmwssb.jalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Vector;

public class SubMenuPage extends Activity implements OnClickListener {

    LinearLayout ll_sub_page;
    Vector<String> dataVec = new Vector<String>();
    Vector<Integer> imageVec = new Vector<Integer>();
    Vector<String> colorVec = new Vector<String>();
    int count = 0;
    LinearLayout[] ll_right_main, ll_left_main, ll_middle_main;
    ImageView iv_logout;
    RelativeLayout rl_lang;
    TextView tv_lang;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sub_menu_page);

        rl_lang = (RelativeLayout) findViewById(R.id.rl_lang);

        tv_lang = (TextView) findViewById(R.id.tv_lang);
        rl_lang.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (v == rl_lang) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (index == 0) {
                            index = 1;
                            Helper.lan_str = "telugu";
                            tv_lang.setText(" En ");
                            LoadData();
                        } else if (index == 1) {
                            tv_lang.setText(" తె ");
                            Helper.lan_str = "english";
                            index = 0;
                            LoadData();
                        }
                    }
                }
                return true;
            }
        });
        ll_sub_page = (LinearLayout) findViewById(R.id.ll_sub_page);

        iv_logout = (ImageView) findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(this);
        if (Helper.lan_str.trim().equalsIgnoreCase("english")) {
            tv_lang.setText(" తె ");
            index = 0;
        } else {
            index = 1;
            tv_lang.setText(" En ");
        }
        LoadData();

    }

    public void LoadData() {
        dataVec.removeAllElements();
        imageVec.removeAllElements();
        colorVec.removeAllElements();

        if (Helper.lan_str.trim().equalsIgnoreCase("english")) {
            dataVec.add("No \nCHLORINE");
            dataVec.add("VALVE \nLEAKAGE");
            dataVec.add("PIPE \nLEAKAGE");
            dataVec.add("POLLUTED \nWATER");
            dataVec.add("SEWERAGE OVERFLOW");
            dataVec.add("MISSING MAN \nHOLE COVER");
            dataVec.add("LOW WATER \nPRESSURE");
            dataVec.add("REQUEST \nMETER");
            dataVec.add("ILLEGAL WATER CONNECTIONS");
        } else {
            dataVec.add("క్లోరిన్  \nలేదు");
            dataVec.add("వాల్వ్  \nలీకేజ్");
            dataVec.add("పైప్  \nలీకేజ్");
            dataVec.add("కలుషిత \nనీరు");
            dataVec.add("మురుగు నీరు పొంగుట");
            dataVec.add("మ్యాన్‌హోల్ మూత లేకుండుట");
            dataVec.add("తక్కువ నీటి \nఒత్తిడి");
            dataVec.add("మీటర్\n అభ్యర్డన");
            dataVec.add("అక్రమ నీటి కనెక్షన్ ఉండుట");
        }

        imageVec.add(R.drawable.no_chlorine);
        imageVec.add(R.drawable.valve_leakage);
        imageVec.add(R.drawable.pipe_leakage);
        imageVec.add(R.drawable.polluted_water);
        imageVec.add(R.drawable.sewarage_overflow);
        imageVec.add(R.drawable.missing_manhole_cover);
        imageVec.add(R.drawable.low_water);
        imageVec.add(R.drawable.request_meter);
        imageVec.add(R.drawable.illegal_water_connection);

        // colorVec.add("#212120");
        // colorVec.add("#007889");
        // colorVec.add("#F38A03");
        // colorVec.add("#C7AE29");
        // colorVec.add("#042F7F");
        // colorVec.add("#007889");
        // colorVec.add("#F6851F");
        // colorVec.add("#3FE0D0");
        // colorVec.add("#716CD2");

        colorVec.add("#070707");
        colorVec.add("#070707");
        colorVec.add("#070707");
        colorVec.add("#070707");
        colorVec.add("#070707");
        colorVec.add("#070707");
        colorVec.add("#070707");
        colorVec.add("#070707");
        colorVec.add("#070707");

        newaddtoGrid();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SubMenuPage.this, HomePage.class));
        finish();
    }

    public void newaddtoGrid() {
        try {
            ll_sub_page.removeAllViews();

            if (!dataVec.isEmpty()) {
                int size = dataVec.size();
                if (size % 3 == 0) {
                    count = (dataVec.size() / 3);

                } else {
                    count = (dataVec.size() / 3) + 1;
                }

                System.out.println("COunt   " + count);

                ll_right_main = new LinearLayout[count];
                ll_left_main = new LinearLayout[count];
                ll_middle_main = new LinearLayout[count];

                for (int i = 0; i < count; i++) {
                    View v = SubMenuPage.this.getLayoutInflater().inflate(
                            R.layout.sub_menu_adapter, null);

                    ll_left_main[i] = (LinearLayout) v
                            .findViewById(R.id.ll_left_main);
                    ll_right_main[i] = (LinearLayout) v
                            .findViewById(R.id.ll_right_main);

                    ll_middle_main[i] = (LinearLayout) v
                            .findViewById(R.id.ll_middle_main);
                    ll_left_main[i].setOnClickListener(this);
                    ll_right_main[i].setOnClickListener(this);
                    ll_middle_main[i].setOnClickListener(this);
                    ImageView img_left = (ImageView) v
                            .findViewById(R.id.img_left);

                    img_left.setImageResource(imageVec.elementAt(3 * i));
                    ImageView img_middle = (ImageView) v
                            .findViewById(R.id.img_middle);
                    TextView tv_left = (TextView) v.findViewById(R.id.tv_left);
                    tv_left.setText(dataVec.elementAt((3 * i)));
                    tv_left.setTextColor(Color.parseColor(colorVec
                            .elementAt(3 * i)));

                    ImageView img_right = (ImageView) v
                            .findViewById(R.id.img_right);

                    TextView tv_middle = (TextView) v
                            .findViewById(R.id.tv_middle);

                    TextView tv_right = (TextView) v
                            .findViewById(R.id.tv_right);

                    TextView tv_sno_right = (TextView) v
                            .findViewById(R.id.tv_sno_right);

                    TextView tv_sno_left = (TextView) v
                            .findViewById(R.id.tv_sno_left);

                    TextView tv_sno_middle = (TextView) v
                            .findViewById(R.id.tv_sno_middle);

                    tv_sno_left.setText("" + ((3 * i) + 1));

                    if (((3 * i) + 1) < dataVec.size()) {
                        img_middle.setImageResource(imageVec
                                .elementAt((3 * i) + 1));
                        tv_middle.setText(dataVec.elementAt((3 * i) + 1));
                        tv_middle.setTextColor(Color.parseColor(colorVec
                                .elementAt((3 * i) + 1)));
                        tv_sno_middle.setText("" + (((3 * i) + 1) + 1));
                    } else {
                        ll_middle_main[i].setVisibility(View.INVISIBLE);
                    }

                    if (((3 * i) + 2) < dataVec.size()) {
                        img_right.setImageResource(imageVec
                                .elementAt((3 * i) + 2));
                        tv_right.setText(dataVec.elementAt((3 * i) + 2));
                        tv_right.setTextColor(Color.parseColor(colorVec
                                .elementAt((3 * i) + 2)));
                        tv_sno_right.setText("" + (((3 * i) + 2) + 1));
                    } else {
                        ll_middle_main[i].setVisibility(View.INVISIBLE);
                    }

                    ll_sub_page.addView(v);
                    ll_sub_page.addView(addVerticalSpace());
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public View addVerticalSpace() {
        View v = null;
        try {
            v = new View(SubMenuPage.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 5);
            v.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    public void Logout() {
        final Dialog dl = new Dialog(SubMenuPage.this);
        dl.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dl.setContentView(R.layout.show_logout);

        Button b_yes = (Button) dl.findViewById(R.id.logout_yes);
        b_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                dl.cancel();

            }
        });
        Button b_no = (Button) dl.findViewById(R.id.logout_no);
        b_no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dl.cancel();
            }
        });
        dl.show();

    }

    @Override
    public void onClick(View v) {
        if (v == iv_logout) {
            Logout();
        } else if (!dataVec.isEmpty()) {
            for (int i = 0; i < count; i++) {

                if (v == ll_left_main[i]) {
                    System.out.println("left i.........." + i);

                    Intent in = new Intent(SubMenuPage.this,
                            EntryActivity.class);
                    in.putExtra("INDEX", "" + (3 * i));
                    in.putExtra("NAME", dataVec.elementAt((3 * i)).toString()
                            .replace("\n", ""));
                    in.putExtra("COLOR", colorVec.elementAt((3 * i)).toString());
                    startActivity(in);
                    finish();

                    break;

                } else if (v == ll_middle_main[i]) {
                    System.out.println("middle i.........." + i);
                    Intent in = new Intent(SubMenuPage.this,
                            EntryActivity.class);
                    in.putExtra("INDEX", "" + ((3 * i) + 1));
                    in.putExtra("NAME", dataVec.elementAt((3 * i) + 1)
                            .toString().replace("\n", ""));
                    in.putExtra("COLOR", colorVec.elementAt((3 * i) + 1)
                            .toString());
                    startActivity(in);
                    finish();

                    break;

                } else if (v == ll_right_main[i]) {
                    System.out.println("right i.........." + i);
                    Intent in = new Intent(SubMenuPage.this,
                            EntryActivity.class);
                    in.putExtra("INDEX", "" + ((3 * i) + 2));
                    in.putExtra("NAME", dataVec.elementAt((3 * i) + 2)
                            .toString().replace("\n", ""));
                    in.putExtra("COLOR", colorVec.elementAt((3 * i) + 2)
                            .toString());
                    startActivity(in);
                    finish();
                    break;
                }

            }
        }
    }

}
