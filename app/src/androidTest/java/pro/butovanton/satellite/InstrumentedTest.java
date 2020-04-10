package pro.butovanton.satellite;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import pro.butovanton.satellite.ui.sats.satsFragment;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.not;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    private static Parser parser;
    private static List<Sat> sats;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("pro.butovanton.satellite", appContext.getPackageName());
    }

    Context context = InstrumentationRegistry.getInstrumentation().getContext();

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void parse_sats() throws IOException, XmlPullParserException {
    parser = new Parser(activityRule.getActivity());
    sats = parser.parse();
    assertTrue(sats.size() > 0);
    for( Sat sat : sats)
        assertTrue(sat.getName() != null);
    }

    @Test
    public void position() {

        assertTrue(sats.size() > 0);

        for (Sat sat : sats) {
            Log.d("DEBUG", "position = " + sat.getPosition());
            assertTrue(sat.getPosition() > 0);
        }
   }

     @Test
     public void camera_test() {
     //    final NavController navController;
    //     navController = NavHostFragment.findNavController(activityRule.getActivity().getFragmentManager().getPrimaryNavigationFragment()).navigate(R.id.action_navigation_home_to_navigation_dashboard);
     }



}


