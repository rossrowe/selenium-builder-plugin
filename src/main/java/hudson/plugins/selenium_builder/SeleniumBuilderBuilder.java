package hudson.plugins.selenium_builder;

import com.saucelabs.ci.SeleniumBuilderManager;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.remoting.Callable;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides the backend logic for running <a href="https://github.com/sebuilder/se-builder">Selenium Builder</a> scripts from Jenkins.
 *
 * The bulk of the logic is contained in the <a href="https://github.com/saucelabs/ci-sauce">ci-sauce</a> library, which is shared across Jenkins/Hudson/Bamboo plugin
 * projects.
 *
 * @author Ross Rowe
 */
public class SeleniumBuilderBuilder extends Builder {

    private static final Logger logger = Logger.getLogger(SeleniumBuilderBuilder.class.getName());

    private String scriptFile;

    @DataBoundConstructor
    public SeleniumBuilderBuilder(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if (scriptFile == null || scriptFile.equals("")) {
            listener.getLogger().println("No script file specified in the job configuration");
            return false;
        }

        try {
            return Computer.currentComputer().getChannel().call(new SeleniumBuilderInvoker(build, listener));
        } catch (IOException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error when invoking Selenium Builder", e);
        }
        return false;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public String getDisplayName() {
            return "Invoke Selenium Builder script";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }

    private class SeleniumBuilderInvoker implements Callable<Boolean, Exception> {
        private AbstractBuild<?, ?> build;
        private BuildListener listener;

        public SeleniumBuilderInvoker(AbstractBuild<?, ?> build, BuildListener listener) {
            this.build = build;
            this.listener = listener;
        }

        public Boolean call() throws InterruptedException, IOException {
            EnvVars env = build.getEnvironment(listener);
            SeleniumBuilderManager seleniumBuilderManager = new SeleniumBuilderManager();
            return seleniumBuilderManager.executeSeleniumBuilder(new File(build.getWorkspace().getRemote(), getScriptFile()), env, listener.getLogger());
        }
    }
}
