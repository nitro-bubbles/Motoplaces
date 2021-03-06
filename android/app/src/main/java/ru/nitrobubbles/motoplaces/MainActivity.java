package ru.nitrobubbles.motoplaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.ArrayList;

import butterknife.ButterKnife;
import ru.nitrobubbles.motoplaces.fragments.AddNewPlaceFragment;
import ru.nitrobubbles.motoplaces.fragments.MotoplacesMapFragment;
import ru.nitrobubbles.motoplaces.fragments.SearchPlaceFragment;
import ru.nitrobubbles.motoplaces.model.Motoplace;
import ru.nitrobubbles.motoplaces.storage.MotoplacesStorage;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity {
    Drawer drawer;
    FragmentManager fragmentManager;
    MotoplacesMapFragment motoplacesMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withStatusBarColor(getResources().getColor(R.color.primary_dark_material_dark))
/*                .withHeader(R.layout.drawer_header)*/
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.search_place)
                                .withIcon(GoogleMaterial.Icon.gmd_search)
                                .withOnDrawerItemClickListener((view, i, iDrawerItem) -> {
                                    fragmentManager
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, new SearchPlaceFragment())
                                            .addToBackStack(null)
                                            .commit();
                                    return false;
                                }),
                        new PrimaryDrawerItem()
                                .withName(R.string.add_point)
                                .withIcon(GoogleMaterial.Icon.gmd_add_circle_outline)
                                .withOnDrawerItemClickListener((view, i, iDrawerItem) -> {
                                    fragmentManager
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, new AddNewPlaceFragment())
                                            .addToBackStack(null)
                                            .commit();
                                    return false;
                                }),
                        new PrimaryDrawerItem().withName(R.string.support).withIcon(GoogleMaterial.Icon.gmd_phone)
                )
                .build();
        init();
    }

    private void init() {
        loadMotoplaces()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(motoplaces -> {
                    MotoplacesStorage.getInstance().setMotoplaces(motoplaces);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, motoplacesMapFragment = new MotoplacesMapFragment()).commit();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if(frag != null)
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                }
            }
        }

        if(motoplacesMapFragment.zoomStoreIsEmpty())
            super.onBackPressed();
    }

    private Observable<ArrayList<Motoplace>> loadMotoplaces(){
        return Observable.just(new ArrayList<>());
    }
}
