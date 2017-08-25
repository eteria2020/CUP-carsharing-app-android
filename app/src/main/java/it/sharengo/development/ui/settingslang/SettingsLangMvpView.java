package it.sharengo.development.ui.settingslang;

import it.sharengo.development.ui.base.presenters.MvpView;

public interface SettingsLangMvpView extends MvpView {

    void showList(String lang);
    void reloadApp();

}
