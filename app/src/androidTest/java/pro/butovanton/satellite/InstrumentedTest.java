package pro.butovanton.satellite;

import android.app.Application;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

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

    private Parser parser;
    private List<Sat> sats;

    @Test
    public void parse_sats() throws IOException, XmlPullParserException {
    parser = new Parser(activityRule.getActivity());
    sats = parser.parse();
    assertTrue(sats.size() > 0);
    for( Sat sat : sats)
        assertTrue(sat.getName() != null);
    }

    @Test
    public void get_position() {

    int position = sats.get(0).getPosition();

    }

}


