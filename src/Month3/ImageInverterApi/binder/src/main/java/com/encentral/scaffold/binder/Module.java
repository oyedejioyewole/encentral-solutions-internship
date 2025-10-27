/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encentral.scaffold.binder;

import com.encentral.image_inverter.modules.ImageModule;
import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;


/**
 * @author poseidon
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {


    @Override
    protected void configure() {

        super.configure();

        bind(BigBang.class).asEagerSingleton();
        bind(ImageModule.class).asEagerSingleton();

    }
}
