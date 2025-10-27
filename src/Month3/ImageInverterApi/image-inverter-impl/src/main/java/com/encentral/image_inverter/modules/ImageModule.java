package com.encentral.image_inverter.modules;

import com.encentral.image_inverter.impl.ImageProcessingService;
import com.encentral.image_inverter.impl.ProcessedImageRepository;
import com.google.inject.AbstractModule;

public class ImageModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ProcessedImageRepository.class).asEagerSingleton();
        bind(ImageProcessingService.class).asEagerSingleton();
    }
}
