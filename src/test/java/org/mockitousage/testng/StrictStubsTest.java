/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.testng.MockitoSettings;
import org.mockito.testng.MockitoTestNGListener;
import org.mockitousage.testng.failuretests.HasUnusedStubs;
import org.mockitousage.testng.failuretests.HasUnusedStubsInSetup;
import org.mockitousage.testng.utils.TestNGRunner;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
// MockitoSettings with default values
@MockitoSettings
public class StrictStubsTest {

    @Mock
    List<String> list;

    @Test
    public void detects_potential_stubbing_problem() {
        when(list.add("a")).thenReturn(true);

        Assertions.assertThatThrownBy(() -> StrictStubsCode.testStrictStubs(list, "b"))
                .isInstanceOf(PotentialStubbingProblem.class);
    }

    @Test public void detects_unused_stubs() {
        //when
        TestNGRunner.Result result = new TestNGRunner().run(HasUnusedStubs.class);

        //then
        assertThat(result.getFailure()).isInstanceOf(UnnecessaryStubbingException.class);
    }

    @Test
    public void detects_unused_stubs_in_setup() {
        //when
        TestNGRunner.Result result = new TestNGRunner().run(HasUnusedStubsInSetup.class);

        //then
        assertThat(result.getFailure()).isInstanceOf(UnnecessaryStubbingException.class);
    }

    @Test
    public void keeps_tests_dry() {
        //when
        when(list.add("a")).thenReturn(true);
        list.add("a");

        //then used stubbing is implicitly verified
        verifyNoMoreInteractions(list);
    }
}
