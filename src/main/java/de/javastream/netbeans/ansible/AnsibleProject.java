package de.javastream.netbeans.ansible;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Philipp Haußleiter <philipp@haussleiter.de>
 */
public class AnsibleProject implements Project {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lkp;

    public AnsibleProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        this.state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                this,
                new Info(),
                new AnsibleProjectLogicalView(this)
            });
        }
        return lkp;
    }

    private final class Info implements ProjectInformation {

        @StaticResource()
        public static final String ANSIBLE_ICON = "de/javastream/netbeans/ansible/ansible.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(ANSIBLE_ICON));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change 
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change 
        }

        @Override
        public Project getProject() {
            return AnsibleProject.this;
        }
    }

    class AnsibleProjectLogicalView implements LogicalViewProvider {

        @StaticResource()
        public static final String ANSIBLE_ICON = "de/javastream/netbeans/ansible/ansible.png";
        private final AnsibleProject project;

        public AnsibleProjectLogicalView(AnsibleProject project) {
            this.project = project;
        }

        @Override
        public Node createLogicalView() {
            try {
                //Obtain the project directory's node: 
                FileObject projectDirectory = project.getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
                //Decorate the project directory's node: 
                return new ProjectNode(nodeOfProjectFolder, project);
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
                //Fallback-the directory couldn't be created - //read-only filesystem or something evil happened 
                return new AbstractNode(Children.LEAF);
            }
        }

        private final class ProjectNode extends FilterNode {

            final AnsibleProject project;

            public ProjectNode(Node node, AnsibleProject project) throws DataObjectNotFoundException {
                super(node,
                        NodeFactorySupport.createCompositeChildren(project, "Projects/de-javastream-ansible/Nodes"),
                        new ProxyLookup(
                                new Lookup[]{
                                    Lookups.singleton(project),
                                    node.getLookup(),}
                        )
                );
                this.project = project;
            }

            @Override
            public Action[] getActions(boolean arg0) {
                List<Action> actions = new ArrayList<Action>();
                actions.add(CommonProjectActions.newFileAction());
                actions.add(CommonProjectActions.copyProjectAction());
                actions.add(CommonProjectActions.deleteProjectAction());
                actions.add(CommonProjectActions.customizeProjectAction());
                actions.add(CommonProjectActions.setProjectConfigurationAction());
                
                // honor 57874 contact
                try {
                    Repository repository = Repository.getDefault();
                    FileSystem sfs = repository.getDefaultFileSystem();
                    FileObject fo = sfs.findResource("Projects/Actions");  // NOI18N
                    if (fo != null) {
                        DataObject dobj = DataObject.find(fo);
                        FolderLookup actionRegistry = new FolderLookup((DataFolder) dobj);
                        Lookup.Template query = new Lookup.Template(Object.class);
                        Lookup lookup = actionRegistry.getLookup();
                        Iterator it = lookup.lookup(query).allInstances().iterator();
                        if (it.hasNext()) {
                            actions.add(null);
                        }
                        while (it.hasNext()) {
                            Object next = it.next();
                            if (next instanceof Action) {
                                actions.add((Action) next);
                            } else if (next instanceof JSeparator) {
                                actions.add(null);
                            }
                        }
                    }
                } catch (DataObjectNotFoundException ex) {
                    // data folder for exiting fileobject expected
                    ErrorManager.getDefault().notify(ex);
                }
                actions.add(CommonProjectActions.closeProjectAction());
                return actions.toArray(new Action[actions.size()]);
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(ANSIBLE_ICON);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return project.getProjectDirectory().getName();
            }
        }

        @Override
        public Node findPath(Node root, Object target) { //leave unimplemented for now 
            return null;
        }
    }
}
