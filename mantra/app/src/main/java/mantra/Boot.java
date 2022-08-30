package mantra;

import java.util.List;
import java.util.Set;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import mantra.model.Model;

/**
 * A boot class that start the application.
 */
public class Boot {

    public static void main(String[] args) {
    	PluginManager pluginManager = new DefaultPluginManager();

        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        List<Model> models = pluginManager.getExtensions(Model.class);
        System.out.println(String.format("Found %d extensions for extension point '%s'", models.size(), Model.class.getName()));
        for (Model model : models) {
            System.out.println(model.getClass().getCanonicalName());
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
    }

}
