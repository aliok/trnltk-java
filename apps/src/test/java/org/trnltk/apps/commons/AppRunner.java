/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.trnltk.apps.commons;

import org.apache.log4j.Level;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

/**
 * A custom runner to run apps which are actually JUnit tests.
 * <p/>
 * The differences are:
 * <ul>
 * <li>apps are not picked by surefire, so they're not run during maven build.</li>
 * <li>you cannot run multiple apps at once.</li>
 * </ul>
 * <p/>
 * <p/>
 * You should use the annotation {@link App} on the methods.
 */
public class AppRunner extends BlockJUnit4ClassRunner {

    public AppRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(App.class);
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(App.class, false, errors);
    }

    @Override
    protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next) {
        // no expected expectation
        return next;
    }

    @Override
    protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
        // no time out
        return next;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        if (this.getDescription().getChildren().size() > 1)
            throw new IllegalStateException("You cannot run multiple apps at once! This runner prevents that!");
        else
            return super.classBlock(notifier);
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        LoggingSettings.setLoggerLevel(LoggingSettings.Piece.EVERYTHING, Level.WARN);
        return super.methodBlock(method);
    }
}
