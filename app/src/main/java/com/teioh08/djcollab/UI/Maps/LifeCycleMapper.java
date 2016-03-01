package com.teioh08.djcollab.UI.Maps;

import android.os.Bundle;

public interface LifeCycleMapper {
    void init(Bundle bundle);

    void onSavedState();

    void onRestoreState();

    void onPause();

    void onResume();

    void onDestroy();
}
