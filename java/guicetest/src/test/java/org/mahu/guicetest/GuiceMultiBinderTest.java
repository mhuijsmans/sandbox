package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;

// This test case explores MultiBinder
// Source: https://github.com/google/guice/wiki/Multibindings
public class GuiceMultiBinderTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            Multibinder<UriSummarizer> uriBinder = Multibinder.newSetBinder(binder(), UriSummarizer.class);
            uriBinder.addBinding().to(NullSummarizer.class);
            uriBinder.addBinding().to(CharacterSummarizer.class);
            uriBinder.addBinding().to(WordSummarizer.class);
        }
    }

    interface UriSummarizer {
        /**
         * Returns a short summary of the URI, or null if this summarizer
         * doesn't know how to summarize the URI.
         */
        String summarize(URI uri);
    }

    static class NullSummarizer implements UriSummarizer {
        public String summarize(URI uri) {
            return null;
        }
    }

    static class CharacterSummarizer implements UriSummarizer {
        public String summarize(URI uri) {
            return "A";
        }
    }

    static class WordSummarizer implements UriSummarizer {
        public String summarize(URI uri) {
            return "Hi";
        }
    }

    static class TestObject {

        private final Set<UriSummarizer> summarizers;

        @Inject
        TestObject(Set<UriSummarizer> summarizers) {
            this.summarizers = summarizers;
        }

        public String prettifyUri(URI uri) {
            // loop through the implementations, looking for one that supports
            // this URI
            StringBuilder sb = new StringBuilder();
            for (UriSummarizer summarizer : summarizers) {
                String summary = summarizer.summarize(uri);
                if (summary != null) {
                    sb.append(summary);
                }
            }
            return sb.toString();
        }
    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);
        final String text = testObject.prettifyUri(new URI("http://localhost.com"));

        assertEquals("AHi", text);
    }

}
