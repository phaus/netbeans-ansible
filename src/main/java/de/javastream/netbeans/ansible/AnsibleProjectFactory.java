package de.javastream.netbeans.ansible;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philipp Haußleiter <philipp@haussleiter.de>
 */
@ServiceProvider(service = ProjectFactory.class)
public class AnsibleProjectFactory implements ProjectFactory {

    public static final String PROJECT_FILE = "inventory";
    public static final String FOLDER_ = "roles";

    /**
     * Check if at least one file in Project Directory has extension "yml" (that
     * is the Playbook).
     *
     * @param projectDirectory
     * @return
     */
    @Override
    public boolean isProject(FileObject projectDirectory) {
        return checkPath(projectDirectory.getPath());
    }

    @Override
    public Project loadProject(FileObject projectDirectory, ProjectState ps) throws IOException {
        return new AnsibleProject(projectDirectory, ps);
    }

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {
    }

    private boolean checkPath(String path) {
        File folder = new File(path);
        File[] children = folder.exists() ? folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.startsWith(".") && (name.endsWith("yml") || name.equals("roles"));
            }
        }) : null;
        return children != null && children.length > 0;
    }

}
