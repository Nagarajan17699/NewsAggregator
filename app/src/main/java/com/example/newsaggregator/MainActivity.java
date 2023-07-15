package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsaggregator.databinding.ActivityMainBinding;
import com.example.newsaggregator.databinding.ListActivityBinding;
import com.example.newsaggregator.databinding.NewsLayoutBinding;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static HashMap<String, SourceClass> sourcesobj;
    private static final String TAG = "MainActivity";
    private Menu menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    //private TextView textView;
    private ActionBarDrawerToggle mDrawerToggle;
    public NewsAdapter newsAdapter;
    private final ArrayList<String> newsNameList = new ArrayList<>();
    private ActivityMainBinding mainBinding;
    private NewsLayoutBinding newsLayoutBinding;
    private ListActivityBinding listActivityBinding;
    private ViewPager2 viewPager2;
    private ArrayList<ArticleClass> articleClasses;
    private static HashMap<String,Integer> colorTable = new HashMap<>();
    private static String current_news_id = "";
    private static String current_news_name = "";
    private static String current_menu_name = "";
    private Bundle savedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        if(savedInstanceState == null){
            current_menu_name="";
            current_news_name="";
            current_news_id="";
        }
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        newsLayoutBinding = NewsLayoutBinding.inflate(getLayoutInflater());
        listActivityBinding = ListActivityBinding.inflate(getLayoutInflater());

        setContentView(mainBinding.getRoot());
        GetData.getSources(this);

        // Make sample items for the drawer list
        mDrawerLayout = mainBinding.drawerLayout; // <== Important!
        mDrawerList = mainBinding.drawerList; // <== Important!

        mDrawerToggle = new ActionBarDrawerToggle(   // <== Important!
                this,                /* host Activity */
                mDrawerLayout,             /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        //ViewPager Access
        ViewPager2 viewPager2 = mainBinding.viewPager;

    }

    //Save and Restore Function
    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {

        if(current_news_id!=null && (!current_news_id.equalsIgnoreCase("")) && current_news_name != null && (!current_news_name.equalsIgnoreCase(""))) {
            savedInstanceState.putString("newsId", current_news_id);
            int current_page_pos = viewPager2.getCurrentItem();
            savedInstanceState.putInt("position", current_page_pos);
            savedInstanceState.putString("newsName", current_news_name);
        }
        Log.d(TAG, "onSaveInstanceState: "+current_menu_name);
        if(current_menu_name!=null && !current_menu_name.equalsIgnoreCase(""))
            savedInstanceState.putString("menuName", current_menu_name);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        current_news_id = savedInstanceState.getString("newsId");
        current_menu_name = savedInstanceState.getString("menuName");
        current_news_name = savedInstanceState.getString("newsName");

        if(current_news_id!=null && !current_news_id.equalsIgnoreCase("")) {
            current_news_name = savedInstanceState.getString("newsName");
            GetData.getNews(current_news_id, this);
        }

        if(current_menu_name!= null && !current_menu_name.equalsIgnoreCase("")) {
            if (current_menu_name.equalsIgnoreCase("all"))
                createDrawer();
            else
                filterDrawer(current_menu_name);
        }
        Log.d(TAG, "onRestoreInstanceState: "+current_news_name);
         if(current_news_name!= null && !current_news_name.equalsIgnoreCase("")) {
             this.setTitle(current_news_name);
         }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig); // <== IMPORTANT
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: Called");
        // Important!
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        String menuTitle = item.getTitle().toString();
        current_menu_name = menuTitle;
        Log.d(TAG, "onOptionsItemSelected: "+menuTitle);
        clearNews();
        current_news_name = "";
        current_news_id = "";
        if(menuTitle.equalsIgnoreCase("all"))
            createDrawer();
        else
            filterDrawer(menuTitle);
        if(savedInstanceState !=null) {
            savedInstanceState.clear();
        }


        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
//        textView.setText(String.format(Locale.getDefault(),
//                "You picked %s", items[position]));
        Log.d(TAG, "selectItem: "+position);
        if(savedInstanceState != null)
            savedInstanceState.clear();
        current_news_id = Objects.requireNonNull(sourcesobj.get(newsNameList.get(position))).getId();
        current_news_name = newsNameList.get(position);
        GetData.getNews(current_news_id,this);
        this.setTitle(current_news_name);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void createDrawer(){
        ArrayList<SourceClass> alsc = new ArrayList<>(sourcesobj.values());
        HashMap<String,String> nameList = new HashMap<>();
        alsc.forEach((x)->nameList.put(x.getName(), x.getCategory()));
        newsNameList.clear();
        nameList.keySet().stream().sorted().forEach(newsNameList::add);
        mDrawerList.setAdapter(new ArrayAdapter(this,   // <== Important!
                R.layout.list_activity, newsNameList){
            public View getView(int pos, @Nullable View parent, @NotNull ViewGroup viewGroup){
                View view = super.getView(pos, parent,viewGroup);
                TextView text = view.findViewById(R.id.drawerText);
                Log.d(TAG, "getView: "+text.getText());
                text.setTextColor(colorTable.get(nameList.get(text.getText())));
                return view;
        }});
        mDrawerList.setOnItemClickListener(   // <== Important!
                (parent, view, position, id) -> selectItem(position)
        );
       // mDrawerList.refreshDrawableState();
        if(current_news_name.equalsIgnoreCase(""))
            this.setTitle("News Gateway ("+newsNameList.size()+")");

        if (getSupportActionBar() != null) {  // <== Important!
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void filterDrawer(String category){
        ArrayList<SourceClass> alsc = new ArrayList<>(sourcesobj.values());
        HashSet<String> nameList = new HashSet<>();
        alsc.stream().filter(x->x.getCategory().equalsIgnoreCase(category)).forEach(x->nameList.add(x.getName()));
        newsNameList.clear();
        nameList.stream().sorted().forEach(newsNameList::add);
        mDrawerList.setAdapter(new ArrayAdapter(this,   // <== Important!
                R.layout.list_activity, newsNameList){
            public View getView(int pos, @Nullable View parent, @NotNull ViewGroup viewGroup){
                View view = super.getView(pos, parent,viewGroup);
                TextView text = view.findViewById(R.id.drawerText);
                Log.d(TAG, "getView: "+text.getText());
                text.setTextColor(colorTable.get(category));
                return view;
            }});

        mDrawerList.setOnItemClickListener(   // <== Important!
                (parent, view, position, id) -> selectItem(position)
        );
        if(current_news_name.equalsIgnoreCase(""))
            this.setTitle("News Gateway ("+newsNameList.size()+")");
        if (getSupportActionBar() != null) {  // <== Important!
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    public void onGetResource(HashMap<String, SourceClass> resources){
        Log.d(TAG, "onGetResource: "+ resources.keySet().stream());
        sourcesobj = resources;
        createMenu();
        if(savedInstanceState != null && savedInstanceState.containsKey("menuName")) {
            String menu_name = savedInstanceState.getString("menuName");
            if(menu_name.equalsIgnoreCase("all"))
                createDrawer();
            else
                filterDrawer(menu_name);
        }
        createDrawer();
    }


    public void onGetNewsData(ArrayList<ArticleClass> articleClasses){

        try {
            viewPager2 = findViewById(R.id.viewPager);
            this.articleClasses = articleClasses;
            newsAdapter = new NewsAdapter(this, articleClasses);
            newsLayoutBinding.newsContent.setMovementMethod(new ScrollingMovementMethod());
            viewPager2.setAdapter(newsAdapter);
            viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            if (savedInstanceState != null && savedInstanceState.containsKey("position") && savedInstanceState.containsKey("newsName")) {
                viewPager2.setCurrentItem((int)savedInstanceState.get("position"));
            }
            newsAdapter.notifyItemChanged(0, articleClasses.size());
        } catch(NullPointerException e) {
            Toast.makeText(this, "Null data received", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public void createMenu(){
        if(menu != null)
            menu.clear();
        ArrayList<SourceClass> alsc = new ArrayList<>(sourcesobj.values());
        HashSet<String> nameList = new HashSet<>();
        nameList.add("All");
        alsc.forEach(x->nameList.add(x.getCategory()));
        createColorTable(nameList);
        nameList.stream().sorted().forEach(x-> {
            Random random = new Random();
            SpannableString spannableString = SpannableString.valueOf(x);
            int text_color = colorTable.get(x);
            spannableString.setSpan(new ForegroundColorSpan(text_color),0,
                    x.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            menu.add(spannableString);
        });
        hideKeyboard();
    }


    public void onClickContent(View view){
        int position = viewPager2.getCurrentItem();
        Log.d(TAG, "onClickContent: "+position);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleClasses.get(position).getUrl()));
        startActivity(intent);
    }

    /*
        Utility Functions
    */

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;

        //Find the currently focused view
        View view = getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null)
            view = new View(this);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void clearNews(){
        viewPager2 = findViewById(R.id.viewPager);
        if(articleClasses != null) {
            articleClasses.clear();
            newsAdapter = new NewsAdapter(this, articleClasses);
            viewPager2.setAdapter(newsAdapter);
            viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            newsAdapter.notifyItemChanged(0, articleClasses.size());
        }
    }

    public void createColorTable(HashSet<String> menu){
        Log.d(TAG, "createColorTable: Create Color Table Called");
        Random rand = new Random();
        menu.stream().forEach(x->{
            int r = 180;
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            int color = Color.rgb(r,g,b);
            colorTable.put(x, color);
        });

        colorTable.forEach((x,y)-> System.out.println(x+" "+y));

    }

}