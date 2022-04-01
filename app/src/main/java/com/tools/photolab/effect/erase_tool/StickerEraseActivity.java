package com.tools.photolab.effect.erase_tool;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tools.photolab.R;
import com.tools.photolab.effect.activity.BaseActivity;
import com.tools.photolab.effect.activity.DripEffectActivity;
import com.tools.photolab.effect.activity.FramesActivity;
import com.tools.photolab.effect.activity.NeonActivity;
import com.tools.photolab.effect.activity.WingsActivity;
import com.tools.photolab.effect.erase_tool.eraser.EraseSView;
import com.tools.photolab.effect.erase_tool.eraser.EraseView;
import com.tools.photolab.effect.erase_tool.eraser.EraseView.ActionListener;
import com.tools.photolab.effect.erase_tool.eraser.EraseView.UndoRedoListener;
import com.tools.photolab.effect.erase_tool.eraser.MultiTouchListener;
import com.tools.photolab.effect.motion_tool.MotionEffectActivity;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;
import com.tools.photolab.effect.support.SupportedClass;


public class StickerEraseActivity extends BaseActivity implements OnClickListener {
    public static Bitmap bgCircleBit = null, bitmap = null;
    public static int curBgType = 1, orgBitHeight, orgBitWidth;
    public static BitmapShader patternBMPshader;
    public static Bitmap b = null;
    public ImageView auto_btn, tbg_img, back_btn, dv1, save_btn, redo_btn, btn_bg, undo_btn, btn_brush, erase_btn, lasso_btn, restore_btn, zoom_btn;
    public RelativeLayout auto_btn_rel, offset_seekbar_lay, rel_auto, rel_seek_container, erase_btn_rel,
            rel_bw, rel_color, rel_desc, rel_lasso, rel_zoom, inside_cut_lay, lasso_btn_rel, main_rel, outside_cut_lay, restore_btn_rel, zoom_btn_rel;
    public Bitmap orgBitmap;
    public EraseView dv;
    public int height;
    public boolean isTutOpen = true, showDialog = false;
    public Animation scale_anim;
    public TextView tv10, tv5, tv6, tv7, tv8, tv9, headertext, txt_desc;
    public int width;
    private View[] btnArr = new View[5];
    private EraseSView eraseSView;
    private LinearLayout lay_lasso_cut, lay_offset_seek, lay_threshold_seek;
    private SeekBar offset_seekbar, offset_seekbar1, offset_seekbar2, radius_seekbar, threshold_seekbar;
    private String openFrom;

    @SuppressLint({"WrongConstant"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_eraser_option);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(StickerEraseActivity.this));
        openFrom = getIntent().getStringExtra(Constants.KEY_OPEN_FROM);

        this.scale_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_anim);
        initUI();
        this.tv5 = (TextView) findViewById(R.id.bg_extract_txt);
        this.tv6 = (TextView) findViewById(R.id.auto_txt);
        this.tv7 = (TextView) findViewById(R.id.txt_lasso);
        this.tv8 = (TextView) findViewById(R.id.erase_txt);
        this.tv9 = (TextView) findViewById(R.id.restore_txt);
        this.tv10 = (TextView) findViewById(R.id.zoom_txt);
        this.isTutOpen = false;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
        this.height = i - ImageUtils.dpToPx(this, 120.0f);
        curBgType = 1;
        this.main_rel.postDelayed(new Runnable() {
            public void run() {
                if (isTutOpen) {
                    tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(StickerEraseActivity.this, R.drawable.tbg3, width, height));
                    bgCircleBit = ImageUtils.getBgCircleBit(StickerEraseActivity.this, R.drawable.tbg3);
                } else {
                    tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(StickerEraseActivity.this, R.drawable.tbg, width, height));
                    StickerEraseActivity.bgCircleBit = ImageUtils.getBgCircleBit(StickerEraseActivity.this, R.drawable.tbg);
                }
                importImageFromUri();
            }
        }, 1000);
    }

    private void initUI() {
        this.rel_auto = (RelativeLayout) findViewById(R.id.rel_auto);
        this.rel_color = (RelativeLayout) findViewById(R.id.rel_color);
        this.rel_bw = (RelativeLayout) findViewById(R.id.rel_bw);
        this.rel_lasso = (RelativeLayout) findViewById(R.id.rel_lasso);
        this.rel_zoom = (RelativeLayout) findViewById(R.id.rel_zoom);
        this.rel_desc = (RelativeLayout) findViewById(R.id.rel_desc);
        this.offset_seekbar_lay = (RelativeLayout) findViewById(R.id.offset_seekbar_lay);
        this.rel_seek_container = (RelativeLayout) findViewById(R.id.rel_seek_container);
        this.auto_btn_rel = (RelativeLayout) findViewById(R.id.rel_auto_btn);
        this.erase_btn_rel = (RelativeLayout) findViewById(R.id.rel_erase_btn);
        this.restore_btn_rel = (RelativeLayout) findViewById(R.id.rel_restore_btn);
        this.lasso_btn_rel = (RelativeLayout) findViewById(R.id.rel_lasso_btn);
        this.zoom_btn_rel = (RelativeLayout) findViewById(R.id.rel_zoom_btn);
        this.headertext = (TextView) findViewById(R.id.headertext);
        this.txt_desc = (TextView) findViewById(R.id.txt_desc);
        this.main_rel = (RelativeLayout) findViewById(R.id.main_rel);
        this.lay_threshold_seek = (LinearLayout) findViewById(R.id.lay_threshold_seek);
        this.lay_offset_seek = (LinearLayout) findViewById(R.id.lay_offset_seek);
        this.lay_lasso_cut = (LinearLayout) findViewById(R.id.lay_lasso_cut);
        this.inside_cut_lay = (RelativeLayout) findViewById(R.id.inside_cut_lay);
        this.outside_cut_lay = (RelativeLayout) findViewById(R.id.outside_cut_lay);
        this.undo_btn = (ImageView) findViewById(R.id.btn_undo);
        this.redo_btn = (ImageView) findViewById(R.id.btn_redo);
        this.auto_btn = (ImageView) findViewById(R.id.btn_auto);
        this.erase_btn = (ImageView) findViewById(R.id.btn_erase);
        this.restore_btn = (ImageView) findViewById(R.id.btn_restore);
        this.lasso_btn = (ImageView) findViewById(R.id.btn_lasso);
        this.zoom_btn = (ImageView) findViewById(R.id.btn_zoom);
        this.back_btn = (ImageView) findViewById(R.id.btn_back);
        this.save_btn = (ImageView) findViewById(R.id.save_image_btn);
        this.btn_bg = (ImageView) findViewById(R.id.btn_bg);
        this.btn_brush = (ImageView) findViewById(R.id.btn_brush);
        this.tbg_img = (ImageView) findViewById(R.id.tbg_img);
        this.btn_brush.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
        this.undo_btn.setOnClickListener(this);
        this.redo_btn.setOnClickListener(this);
        this.undo_btn.setEnabled(false);
        this.redo_btn.setEnabled(false);
        this.save_btn.setOnClickListener(this);
        this.btn_bg.setOnClickListener(this);
        this.erase_btn.setOnClickListener(this);
        this.auto_btn.setOnClickListener(this);
        this.lasso_btn.setOnClickListener(this);
        this.restore_btn.setOnClickListener(this);
        this.zoom_btn.setOnClickListener(this);
        this.inside_cut_lay.setOnClickListener(this);
        this.outside_cut_lay.setOnClickListener(this);
        this.btnArr[0] = findViewById(R.id.lay_auto_btn);
        this.btnArr[1] = findViewById(R.id.lay_erase_btn);
        this.btnArr[2] = findViewById(R.id.lay_restore_btn);
        this.btnArr[3] = findViewById(R.id.lay_lasso_btn);
        this.btnArr[4] = findViewById(R.id.lay_zoom_btn);
        this.offset_seekbar = (SeekBar) findViewById(R.id.offset_seekbar);
        this.offset_seekbar1 = (SeekBar) findViewById(R.id.offset_seekbar1);
        this.offset_seekbar2 = (SeekBar) findViewById(R.id.offset_seekbar2);
        this.offset_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (dv != null) {
                    dv.setOffset(i - 150);
                    dv.invalidate();
                }
            }
        });
        this.offset_seekbar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (dv != null) {
                    dv.setOffset(i - 150);
                    dv.invalidate();
                }
            }
        });
        this.offset_seekbar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (dv != null) {
                    dv.setOffset(i - 150);
                    dv.invalidate();
                }
            }
        });
        this.radius_seekbar = (SeekBar) findViewById(R.id.radius_seekbar);
        this.radius_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (dv != null) {
                    dv.setRadius(i + 2);
                    dv.invalidate();
                }
            }
        });
        this.threshold_seekbar = (SeekBar) findViewById(R.id.threshold_seekbar);
        this.threshold_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (dv != null) {
                    dv.setThreshold(seekBar.getProgress() + 10);
                    dv.updateThreshHold();
                }
            }
        });
    }

    @SuppressLint({"WrongConstant"})
    public void onClick(View view) {
        if (this.dv != null || view.getId() == R.id.btn_back) {
            String str = "...";
            String str2 = "";
            switch (view.getId()) {
                case R.id.btn_auto:
                case R.id.rel_auto_btn:
                    setSelected(R.id.lay_auto_btn);
                    setClickUnSelected();
//                    this.auto_btn.setBackgroundResource(R.drawable.ic_slated_auto);
//                    this.tv1.setTextColor(getResources().getColor(R.color.select_new));
//                    this.tv6.setTextColor(getResources().getColor(R.color.select_new));
                    this.dv.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.dv.setMODE(2);
                    this.dv.invalidate();
                    return;
                case R.id.btn_back:
                    onBackPressed();
                    return;
                case R.id.btn_bg:
                    changeBG();
                    return;
                case R.id.btn_brush:
                    EraseView eraseView = this.dv;
                    if (eraseView != null) {
                        if (eraseView.isRectBrushEnable()) {
                            this.dv.enableRectBrush(false);
                            this.dv.invalidate();
                            this.btn_brush.setImageResource(R.drawable.ic_square);
                            return;
                        }
                        this.dv.enableRectBrush(true);
                        this.btn_brush.setImageResource(R.drawable.ic_circles);
                        this.dv.invalidate();
                        return;
                    }
                    return;
                case R.id.btn_erase:
                case R.id.rel_erase_btn:
                    setSelected(R.id.lay_erase_btn);
                    setClickUnSelected();
                    this.dv.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.dv.setMODE(1);
                    this.dv.invalidate();
                    if (this.isTutOpen) {
                        this.rel_color.setVisibility(View.GONE);
                        this.rel_color.clearAnimation();
                        this.txt_desc.setText(getResources().getString(R.string.drag_color));
                        this.rel_desc.setVisibility(View.VISIBLE);
                        this.rel_desc.startAnimation(this.scale_anim);
                        this.erase_btn_rel.clearAnimation();
                    }
                    return;
                case R.id.btn_lasso:
                case R.id.rel_lasso_btn:
                    this.offset_seekbar_lay.setVisibility(View.VISIBLE);
                    setSelected(R.id.lay_lasso_btn);
                    setClickUnSelected();
//                    this.lasso_btn.setBackgroundResource(R.drawable.ic_slated_extract);
//                    this.tv7.setTextColor(getResources().getColor(R.color.select_new));
                    this.dv.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.dv.setMODE(3);
                    this.dv.invalidate();
                    if (this.isTutOpen) {
                        this.rel_lasso.setVisibility(View.GONE);
                        this.rel_lasso.clearAnimation();
                        this.txt_desc.setText(getResources().getString(R.string.msg_draw_lasso));
                        this.rel_desc.setVisibility(View.VISIBLE);
                        this.rel_desc.startAnimation(this.scale_anim);
                        this.lasso_btn_rel.clearAnimation();
                    }
                    return;
                case R.id.btn_redo:
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.redoing));
                    sb.append(str);
                    final ProgressDialog show = ProgressDialog.show(this, str2, sb.toString(), true);
                    show.setCancelable(false);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        dv.redoChange();
                                    }
                                });
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            show.dismiss();
                        }
                    }).start();
                    return;
                case R.id.btn_restore:
                case R.id.rel_restore_btn:
                    setSelected(R.id.lay_restore_btn);
                    setClickUnSelected();
//                    this.restore_btn.setBackgroundResource(R.drawable.ic_slated_restore);
//                    this.tv4.setTextColor(getResources().getColor(R.color.select_new));
//                    this.tv9.setTextColor(getResources().getColor(R.color.select_new));
                    this.dv.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.dv.setMODE(4);
                    this.dv.invalidate();
                    if (this.isTutOpen) {
                        this.rel_bw.setVisibility(View.GONE);
                        this.rel_bw.clearAnimation();
                        this.txt_desc.setText(getResources().getString(R.string.drag_bw));
                        this.rel_desc.setVisibility(View.VISIBLE);
                        this.rel_desc.startAnimation(this.scale_anim);
                        this.restore_btn_rel.clearAnimation();
                    }
                    return;
                case R.id.btn_undo:
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(getString(R.string.undoing));
                    sb2.append(str);
                    final ProgressDialog show2 = ProgressDialog.show(this, str2, sb2.toString(), true);
                    show2.setCancelable(false);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        dv.undoChange();
                                    }
                                });
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            show2.dismiss();
                        }
                    }).start();
                    return;
                case R.id.btn_zoom:
                case R.id.rel_zoom_btn:
                    this.dv.enableTouchClear(false);
                    this.main_rel.setOnTouchListener(new MultiTouchListener());
                    setSelected(R.id.lay_zoom_btn);
                    setClickUnSelected();
//                    this.zoom_btn.setBackgroundResource(R.drawable.ic_slated_zoom);
//                    this.tv10.setTextColor(getResources().getColor(R.color.select_new));
                    this.dv.setMODE(0);
                    this.dv.invalidate();
                case R.id.inside_cut_lay:
                    this.offset_seekbar_lay.setVisibility(View.VISIBLE);
                    this.dv.enableInsideCut(true);
                    this.inside_cut_lay.clearAnimation();
                    this.outside_cut_lay.clearAnimation();
                    return;
                case R.id.outside_cut_lay:
                    this.offset_seekbar_lay.setVisibility(View.VISIBLE);
                    this.dv.enableInsideCut(false);
                    this.inside_cut_lay.clearAnimation();
                    this.outside_cut_lay.clearAnimation();
                    return;
                case R.id.save_image_btn:
                    bitmap = this.dv.getFinalBitmap();
                    if (bitmap != null) {
                        try {
                            int dpToPx = ImageUtils.dpToPx(StickerEraseActivity.this, 42.0f);
                            bitmap = ImageUtils.resizeBitmap(bitmap, orgBitWidth + dpToPx + dpToPx, orgBitHeight + dpToPx + dpToPx);
                            int i = dpToPx + dpToPx;
                            bitmap = Bitmap.createBitmap(bitmap, dpToPx, dpToPx, bitmap.getWidth() - i, bitmap.getHeight() - i);
                            bitmap = Bitmap.createScaledBitmap(bitmap, orgBitWidth, orgBitHeight, true);
                            bitmap = ImageUtils.bitmapmasking(orgBitmap, bitmap);

                            if (openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_NEON)) {
                                NeonActivity.eraserResultBmp = bitmap;
                            } else if (openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_FRAME)) {
                                FramesActivity.eraserResultBmp = bitmap;
                            } else if (openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_WINGS)) {
                                WingsActivity.eraserResultBmp = bitmap;
                            } else if (openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_DRIP)) {
                                DripEffectActivity.eraserResultBmp = bitmap;
                            } else if (openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_MOTION)) {
                                MotionEffectActivity.eraserResultBmp = bitmap;
                            }
                            setResult(RESULT_OK);
                            finish();
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                    } else {
                        finish();
                    }
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.import_img_warning), Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_unsaved_work);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(((SupportedClass.getWidth(StickerEraseActivity.this) / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private void changeBG() {
        int i = curBgType;
        if (i == 1) {
            curBgType = 2;
            this.tbg_img.setImageBitmap(null);
            this.tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg1, this.width, this.height));
            this.btn_bg.setImageResource(R.drawable.tbg2);
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg1);
        } else if (i == 2) {
            curBgType = 3;
            this.tbg_img.setImageBitmap(null);
            this.tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg2, this.width, this.height));
            this.btn_bg.setImageResource(R.drawable.tbg3);
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg2);
        } else if (i == 3) {
            curBgType = 4;
            this.tbg_img.setImageBitmap(null);
            this.tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg3, this.width, this.height));
//            this.btn_bg.setImageResource(R.drawable.tbg4);
            this.btn_bg.setImageResource(R.drawable.tbg1);
            //for user view   - (image and background both are set here white so)
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg3);
        } else if (i == 4) {
            curBgType = 5;
            this.tbg_img.setImageBitmap(null);
            this.tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg4, this.width, this.height));
            this.btn_bg.setImageResource(R.drawable.tbg5);
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg4);
        } else if (i == 5) {
            curBgType = 6;
            this.tbg_img.setImageBitmap(null);
            this.tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg5, this.width, this.height));
            this.btn_bg.setImageResource(R.drawable.tbg);
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg5);
        } else if (i == 6) {
            curBgType = 1;
            this.tbg_img.setImageBitmap(null);
            this.tbg_img.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg, this.width, this.height));
            this.btn_bg.setImageResource(R.drawable.tbg1);
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg);
        }
    }

    public void importImageFromUri() {
        this.showDialog = false;
        final ProgressDialog show = ProgressDialog.show(this, "", getResources().getString(R.string.importing_image), true);
        show.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (b == null) {
                        showDialog = true;
                    } else {
                        orgBitmap = b.copy(b.getConfig(), true);
                        int dpToPx = ImageUtils.dpToPx(StickerEraseActivity.this, 42.0f);
                        StickerEraseActivity.orgBitWidth = b.getWidth();
                        StickerEraseActivity.orgBitHeight = b.getHeight();
                        Bitmap createBitmap = Bitmap.createBitmap(b.getWidth() + dpToPx + dpToPx, b.getHeight() + dpToPx + dpToPx, b.getConfig());
                        Canvas canvas = new Canvas(createBitmap);
                        canvas.drawColor(0);
                        float f = (float) dpToPx;
                        canvas.drawBitmap(b, f, f, null);
                        b = createBitmap;
                        if (b.getWidth() > width || b.getHeight() > height || (b.getWidth() < width && b.getHeight() < height)) {
                            b = ImageUtils.resizeBitmap(b, width, height);
                        }
                    }
                    Thread.sleep(1000);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    showDialog = true;
                    show.dismiss();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    showDialog = true;
                    show.dismiss();
                }
                show.dismiss();
            }
        }).start();
        show.setOnDismissListener(new OnDismissListener() {
            @SuppressLint({"WrongConstant"})
            public void onDismiss(DialogInterface dialogInterface) {
                if (showDialog) {
                    StickerEraseActivity stickerRemoveActivity = StickerEraseActivity.this;
                    Toast.makeText(stickerRemoveActivity, stickerRemoveActivity.getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                String str = "";
                ConstantsApp.rewid = str;
                ConstantsApp.uri = str;
                ConstantsApp.bitmapSticker = null;
                setImageBitmap();
            }
        });
    }

    @SuppressLint({"WrongConstant"})
    public void setImageBitmap() {
        this.dv = new EraseView(this);
        this.dv1 = new ImageView(this);
        this.dv.setImageBitmap(this.b);
        this.dv1.setImageBitmap(getGreenLayerBitmap(this.b));
        this.dv.enableTouchClear(false);
        this.dv.setMODE(0);
        this.dv.invalidate();
        this.offset_seekbar.setProgress(225);
        this.radius_seekbar.setProgress(18);
        this.threshold_seekbar.setProgress(20);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_rel_parent);
        LayoutParams layoutParams = (LayoutParams) relativeLayout.getLayoutParams();
        this.eraseSView = new EraseSView(this);
        this.eraseSView.setLayoutParams(new LayoutParams(-1, -1));
        relativeLayout.addView(this.eraseSView);
        this.dv.setEraseSView(this.eraseSView);
        this.main_rel.removeAllViews();
        this.main_rel.setScaleX(1.0f);
        this.main_rel.setScaleY(1.0f);
        this.main_rel.addView(this.dv1);
        this.main_rel.addView(this.dv);
        relativeLayout.setLayoutParams(layoutParams);
        this.dv.invalidate();
        this.dv1.setVisibility(View.GONE);
        this.dv.setUndoRedoListener(new UndoRedoListener() {
            public void enableUndo(boolean z, int i) {
                if (z) {
                    StickerEraseActivity stickerRemoveActivity = StickerEraseActivity.this;
                    stickerRemoveActivity.setBGDrawable(i, undo_btn, R.drawable.ic_undo_new, z);
                    return;
                }
                StickerEraseActivity stickerRemoveActivity2 = StickerEraseActivity.this;
                stickerRemoveActivity2.setBGDrawable(i, undo_btn, R.drawable.ic_undo_new1, z);
            }

            public void enableRedo(boolean z, int i) {
                if (z) {
                    StickerEraseActivity stickerRemoveActivity = StickerEraseActivity.this;
                    stickerRemoveActivity.setBGDrawable(i, redo_btn, R.drawable.ic_redo_new, z);
                    return;
                }
                StickerEraseActivity stickerRemoveActivity2 = StickerEraseActivity.this;
                stickerRemoveActivity2.setBGDrawable(i, redo_btn, R.drawable.ic_redo_new1, z);
            }
        });
        this.b.recycle();
        this.dv.setActionListener(new ActionListener() {
            public void onActionCompleted(final int i) {
                runOnUiThread(new Runnable() {
                    @SuppressLint({"WrongConstant"})
                    public void run() {
                        if (i == 5) {
                            offset_seekbar_lay.setVisibility(View.GONE);
                        }
                    }
                });
            }

            public void onAction(final int i) {
                runOnUiThread(new Runnable() {
                    @SuppressLint({"WrongConstant"})
                    public void run() {
                        if (i == 0) {
                            rel_seek_container.setVisibility(View.GONE);
                        }
                        if (i == 1) {
                            rel_seek_container.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        erase_btn.performClick();
    }

    public void setBGDrawable(int i, ImageView imageView, int i2, boolean z) {
        final ImageView imageView2 = imageView;
        final int i3 = i2;
        final boolean z2 = z;
        runOnUiThread(new Runnable() {
            public void run() {
                imageView2.setImageResource(i3);
                imageView2.setEnabled(z2);
            }
        });
    }

    public Bitmap getGreenLayerBitmap(Bitmap bitmap2) {
        Paint paint = new Paint();
        paint.setColor(-16711936);
        paint.setAlpha(80);
        int dpToPx = ImageUtils.dpToPx(this, 42.0f);
        Bitmap createBitmap = Bitmap.createBitmap(orgBitWidth + dpToPx + dpToPx, orgBitHeight + dpToPx + dpToPx, bitmap2.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(0);
        float f = (float) dpToPx;
        canvas.drawBitmap(this.orgBitmap, f, f, null);
        canvas.drawRect(f, f, (float) (orgBitWidth + dpToPx), (float) (orgBitHeight + dpToPx), paint);
        Bitmap createBitmap2 = Bitmap.createBitmap(orgBitWidth + dpToPx + dpToPx, orgBitHeight + dpToPx + dpToPx, bitmap2.getConfig());
        Canvas canvas2 = new Canvas(createBitmap2);
        canvas2.drawColor(0);
        canvas2.drawBitmap(this.orgBitmap, f, f, null);
        patternBMPshader = new BitmapShader(ImageUtils.resizeBitmap(createBitmap2, this.width, this.height), TileMode.REPEAT, TileMode.REPEAT);
        return ImageUtils.resizeBitmap(createBitmap, this.width, this.height);
    }

    public void setClickUnSelected() {
        this.btn_bg.setImageResource(R.drawable.ic_bg);
        this.auto_btn.setImageResource(R.drawable.ic_auto);
        this.lasso_btn.setImageResource(R.drawable.ic_extract);
        this.erase_btn.setImageResource(R.drawable.ic_manual);
        this.restore_btn.setImageResource(R.drawable.ic_restore);
        this.zoom_btn.setImageResource(R.drawable.ic_zoom_edit);
        this.tv5.setTextColor(getResources().getColor(R.color.colorBlack));
        this.tv6.setTextColor(getResources().getColor(R.color.colorBlack));
        this.tv7.setTextColor(getResources().getColor(R.color.colorBlack));
        this.tv8.setTextColor(getResources().getColor(R.color.colorBlack));
        this.tv9.setTextColor(getResources().getColor(R.color.colorBlack));
        this.tv10.setTextColor(getResources().getColor(R.color.colorBlack));
    }

    @SuppressLint({"WrongConstant"})
    public void setSelected(int i) {
        if (i == R.id.lay_erase_btn) {
            this.offset_seekbar.setProgress(this.dv.getOffset() + 150);
            this.lay_offset_seek.setVisibility(View.VISIBLE);
            this.lay_threshold_seek.setVisibility(View.GONE);
            this.lay_lasso_cut.setVisibility(View.GONE);
            this.headertext.setText(getResources().getString(R.string.erase));
        }
        if (i == R.id.lay_auto_btn) {
            this.offset_seekbar1.setProgress(this.dv.getOffset() + 150);
            this.lay_offset_seek.setVisibility(View.GONE);
            this.lay_threshold_seek.setVisibility(View.VISIBLE);
            this.lay_lasso_cut.setVisibility(View.GONE);
            this.headertext.setText(getResources().getString(R.string.auto));
        }
        if (i == R.id.lay_lasso_btn) {
            this.offset_seekbar2.setProgress(this.dv.getOffset() + 150);
            this.lay_offset_seek.setVisibility(View.GONE);
            this.lay_threshold_seek.setVisibility(View.GONE);
            this.lay_lasso_cut.setVisibility(View.VISIBLE);
            this.headertext.setText(getResources().getString(R.string.lasso));
        }
        if (i == R.id.lay_restore_btn) {
            this.offset_seekbar.setProgress(this.dv.getOffset() + 150);
            this.lay_offset_seek.setVisibility(View.VISIBLE);
            this.lay_threshold_seek.setVisibility(View.GONE);
            this.lay_lasso_cut.setVisibility(View.GONE);
            this.headertext.setText(getResources().getString(R.string.restore));
        }
        if (i == R.id.lay_zoom_btn) {
            this.lay_offset_seek.setVisibility(View.GONE);
            this.lay_threshold_seek.setVisibility(View.GONE);
            this.lay_lasso_cut.setVisibility(View.GONE);
            this.headertext.setText(getResources().getString(R.string.zoom));
        }
        if (i == R.id.lay_restore_btn) {
            this.dv1.setVisibility(View.VISIBLE);
        } else {
            this.dv1.setVisibility(View.GONE);
        }
        if (i != R.id.lay_zoom_btn) {
            this.dv.updateOnScale(this.main_rel.getScaleX());
        }
    }


    public void onDestroy() {
        Bitmap bitmap2 = this.b;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.b = null;
        }
        try {
            if (!isFinishing() && this.dv.pd != null && this.dv.pd.isShowing()) {
                this.dv.pd.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        super.onDestroy();
    }
}
