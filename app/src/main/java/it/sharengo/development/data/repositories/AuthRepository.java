package it.sharengo.development.data.repositories;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.models.User;

@Singleton
public class AuthRepository {

    public static final String TAG = AuthRepository.class.getSimpleName();


    public boolean auth = true;
    public User userAuth = new User("francesco.galatro@gmail.com", "AppTest2017", "508c82b943ae51118d905553b8213c8a");

    @Inject
    public AuthRepository() {
    }



}
