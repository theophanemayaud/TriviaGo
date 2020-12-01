package com.epfl.triviago;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class OTDBService extends IntentService {

    public static final String ACTION_GET_QUESTION = "com.epfl.triviago.action.get_question";
//    private static final String ACTION_BAZ = "com.epfl.triviago.action.BAZ";

    public static final String DIFFICULTY_PARAM = "com.epfl.triviago.extra.difficulty";
    public static final String CATEGORY_PARAM = "com.epfl.triviago.extra.category";

    public OTDBService() {
        super("OTDBService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_QUESTION.equals(action)) {
                final String difficulty = intent.getStringExtra(DIFFICULTY_PARAM);
                final String category = intent.getStringExtra(CATEGORY_PARAM);
                handleActionFoo(difficulty, category);
            }
//            else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

//    /**
//     * Starts this service to perform action Baz with the given parameters. If
//     * the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, OTDBService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}