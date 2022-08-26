package ctwedge.mantra;

import java.util.List;
import java.util.Set;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import ctwedge.mantra.model.Model;

/**
 * A boot class that start the application.
 */
public class Boot {

    public static void main(String[] args) {
        // create the plugin manager
    	System.setProperty(DefaultPluginManager.PLUGINS_DIR_PROPERTY_NAME, "pluginz");
        PluginManager pluginManager = new DefaultPluginManager();
        // load the plugins
        pluginManager.loadPlugins();

        // enable a disabled plugin
//        pluginManager.enablePlugin("welcome-plugin");

        // start (active/resolved) the plugins
        pluginManager.startPlugins();

        // retrieves the extensions for Greeting extension point
        List<Model> models = pluginManager.getExtensions(Model.class);
        System.out.println(String.format("Found %d extensions for extension point '%s'", models.size(), Model.class.getName()));
        for (Model model : models) {
            model.loadModelFromPath(null);
        }

        System.out.println("Extension classes by classpath:");
        List<Class<? extends Model>> modelsClasses = pluginManager.getExtensionClasses(Model.class);
        for (Class<? extends Model> model : modelsClasses) {
            System.out.println("   Class: " + model.getCanonicalName());
        }

        // print extensions ids for each started plugin
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            System.out.println(String.format("Extensions added by plugin '%s':", pluginId));
            Set<String> extensionClassNames = pluginManager.getExtensionClassNames(pluginId);
            for (String extension : extensionClassNames) {
                System.out.println("   " + extension);
            }
        }


        // stop the plugins
        pluginManager.stopPlugins();
        /*
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                pluginManager.stopPlugins();
            }

        });
        */
    }

}
