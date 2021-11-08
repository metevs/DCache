package com.metevs.dcache.loader.util;

import org.springframework.cglib.beans.BeanCopier;


public class BeanCopyUtils {


    public static void copy(Object source, Class sourceClass, Object target, Class targetClass) {

        if (source == null || target == null) {
            return;
        }

        BeanCopier copier = BeanCopier.create(sourceClass, targetClass, false);

        copier.copy(source, target, null);

    }


    public static void copy(Object source, Object target) {

        if (source == null || target == null) {
            return;
        }

        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);

        copier.copy(source, target, null);

    }


}
