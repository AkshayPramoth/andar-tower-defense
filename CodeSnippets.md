# Introduction #

We have spent lot of time for creating OpenGL HUD (Head-Up-Display, game controls and informations) for our game, trying to put some text or images over the camera view; all without success.

The solution is very simple - use a normal view (Relative / Linear / etc) with normal TextViews, ImageViews, Buttons, etc. over the mail.xml layout.


# Details #

**Create a local variable for LayoutInflater**:

```
	LayoutInflater controlInflater = null;
```


**Put this in onCreate() of MyARactivity**:
```
        // add layout 
        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl
            = new LayoutParams(LayoutParams.FILL_PARENT,
            LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
```


**This is the xml-layout control.xml:**

```
<?xml version="1.0" encoding="utf-8"?>

<RelativeLayout android:layout_width="fill_parent"
    android:layout_height="fill_parent" xmlns:android="http://schemas.android.com/apk/res/android">

    <TextView android:id="@+id/enemies_killed"
        android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:text="enemies passed: 2" android:textSize="18sp"
        android:textColor="#ffcccccc" android:layout_below="@+id/enemies_passed"
        android:layout_alignParentLeft="true" android:layout_marginLeft="5dip" />
    <TextView android:id="@+id/enemies_passed"
        android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:text="enemies killed: 100500" android:textSize="18sp"
        android:textColor="#ffcccccc" android:layout_alignParentTop="true"
        android:layout_alignParentLeft="true" android:layout_marginLeft="5dip" />
    <TextView android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:text="whole game progress"
        android:textSize="12sp" android:textColor="#ffcccccc"
        android:layout_alignParentTop="true" android:layout_alignParentRight="true"
        android:layout_marginTop="25dip" android:layout_marginRight="5dip" />

    <ProgressBar android:id="@+id/progressBar"
        android:layout_width="150dip" android:layout_height="8dip"
        style="?android:attr/progressBarStyleHorizontal"
        android:indeterminateOnly="false" android:max="100" android:progress="37"
        android:layout_alignParentTop="true" android:layout_alignParentRight="true"
        android:layout_marginTop="11dip" android:layout_marginRight="5dip" />
</RelativeLayout>
```