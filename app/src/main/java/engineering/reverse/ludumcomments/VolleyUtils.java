package engineering.reverse.ludumcomments;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

/**
 * @author Rishi Raj
 */

public class VolleyUtils extends DefaultRetryPolicy {
    private static RetryPolicy policy;
    public static RetryPolicy getRetryPolicy() {
        if(policy == null) {
            policy = new DefaultRetryPolicy(
                    10000,
                    2,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        return policy;
    }
}
