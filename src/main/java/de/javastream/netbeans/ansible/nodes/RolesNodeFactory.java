package de.javastream.netbeans.ansible.nodes;

import de.javastream.netbeans.ansible.AnsibleProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Philipp Haußleiter <philipp@haussleiter.de>
 */
@NodeFactory.Registration(projectType = "de-javastream-ansible", position = 10)
public class RolesNodeFactory implements NodeFactory {

    private final static String ROLES_FOLDER_NAME = "roles";
    @Override
    public NodeList<?> createNodes(Project project) {
        AnsibleProject p = project.getLookup().lookup(AnsibleProject.class);
        assert p != null;
        return new RolesNodeList(p);
    }

    private class RolesNodeList implements NodeList<Node> {

        AnsibleProject project;

        public RolesNodeList(AnsibleProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            FileObject rolesFolder = project.getProjectDirectory().getFileObject(ROLES_FOLDER_NAME);
            List<Node> result = new ArrayList<>();
            if (rolesFolder != null) {
                for (FileObject roleFolder : rolesFolder.getChildren()) {
                    try {
                        result.add(DataObject.find(roleFolder).getNodeDelegate());
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            return result;
        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
    }
}
