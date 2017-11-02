package it.sharengo.eteria.ui.settingslang;

import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface SettingsLangMvpView extends MvpView {

    void showList(String lang);
    void reloadApp();

}
