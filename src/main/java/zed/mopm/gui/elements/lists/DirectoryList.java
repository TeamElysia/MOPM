package zed.mopm.gui.elements.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.DirectoryTree;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.menus.base.SelectMenuBase;
import zed.mopm.gui.menus.base.ServerSelectMenu;
import zed.mopm.gui.menus.base.WorldSelectMenu;
import zed.mopm.gui.menus.mutators.directory.EditDirectoryMenu;
import zed.mopm.gui.utils.GuiUtils;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.File;
import java.util.*;
import java.util.List;

import static zed.mopm.gui.utils.constants.ColorConsts.BACKGROUND_COLOR;

public class DirectoryList<K extends IFolderPath>
        extends GuiListExtended
        implements IModifiableList {

    //-----Consts:--------------------------------------//

    /**
     * The top location where the list starts.
     */
    private static final int LIST_TOP = 32;
    /**
     * The height offset of the list.
     */
    private static final int LIST_OFFSET = 64;

    //-----Fields:--------------------------------------//

    /**
     * The menu the list is contained in.
     */
    private SelectMenuBase container;

    /**
     * The file the list information will be saved to.
     */
    private File saveFile;
    /**
     * The base directory.
     */
    private DirectoryTree<K> base;
    /**
     * The current directory being looked at.
     */
    private DirectoryTree<K> currentDir;
    /**
     * The string path of the current directory.
     */
    private Deque<String> currentPath = new LinkedList<>();
    /**
     * Determines if this list is a clone.
     */
    private boolean clone = false;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new directory list.
     * @param parentIn     The selection menu of this list.
     * @param widthIn      The width of this list.
     * @param heightIn     The height of this list.
     * @param topIn        The horizontal top location of this list.
     * @param slotHeightIn The slot height of list entries.
     * @param saveIn       The save file of the list data.
     */
    public DirectoryList(
            final SelectMenuBase parentIn,
            final int widthIn,
            final int heightIn,
            final int topIn,
            final int slotHeightIn,
            final File saveIn
    ) {
        this(widthIn, heightIn, topIn, slotHeightIn);
        if (parentIn.getInvokeScreen() instanceof WorldSelectMenu) {
            saveFile = new File(saveIn, MOPMLiterals.MOPM_SSP);
        } else if (parentIn.getInvokeScreen() instanceof ServerSelectMenu) {
            saveFile = new File(saveIn, MOPMLiterals.MOPM_SMP);
        }

        base.load(saveFile);
        container = parentIn;
    }

    /**
     * This helps construct a directory list.
     * @param widthIn      The width of the list.
     * @param heightIn     The height of the list.
     * @param topIn        The horizontal top location of the list.
     * @param slotHeightIn The slot height of list entries.
     */
    private DirectoryList(
            final int widthIn,
            final int heightIn,
            final int topIn,
            final int slotHeightIn
    ) {
        super(
                Minecraft.getMinecraft(),
                widthIn,
                heightIn,
                topIn,
                0,
                slotHeightIn
        );
        base = new DirectoryTree(MOPMLiterals.BASE_DIR_NAME);
        this.currentDir = base;
        this.selectedElement = currentDir.getDepth();
    }

    /**
     * Makes a deep copy of an instance of DirectoryList.
     *
     * @param copyFrom a copy of the folder list
     */
    public DirectoryList(final DirectoryList copyFrom) {
        this(
                copyFrom.width,
                copyFrom.height,
                copyFrom.top,
                copyFrom.slotHeight
        );
        this.container = null;
        this.clone = true;
        this.headerPadding = 1;
        this.base = new DirectoryTree(copyFrom.base);
        this.currentDir = new DirectoryTree(copyFrom.currentDir);
        this.currentPath = new LinkedList<>();
        this.currentPath.addAll(copyFrom.currentPath);
    }

    //-----Overridden Methods:--------------------------//


    //:: GuiListExtended
    //:::::::::::::::::::::::::::::://

    /**
     * If a directory entry is entered into, update the current
     * directory being looked at, and addServerData the current
     * directory location to the path.
     *
     * @param slotIndex     slot index
     * @param isDoubleClick has been double clicked
     * @param mouseX        mouse pos x
     * @param mouseY        mouse pos y
     */
    @Override
    protected void elementClicked(
            final int slotIndex,
            final boolean isDoubleClick,
            final int mouseX,
            final int mouseY
    ) {
        if (isDoubleClick) {
            currentDir = currentDir.stepDown(slotIndex);
            currentPath.push(currentDir.getUniqueName());
            if (!clone) {
                container.refreshDirectoryEntryList();
            }
        }
    }

    /**
     * Handles mouse clicks.
     * If x area was successfully clicked, delete the directory located
     * at the cursor's coordinates. If the mouse was right clicked, open
     * up the context menu to choose more actions.
     *
     * @param mouseX     mouse pos x
     * @param mouseY     mouse pos y
     * @param mouseEvent the mouse event
     * @return Returns true if a directory entry was successfully clicked.
     * Returns false otherwise.
     */
    @Override
    public boolean mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseEvent
    ) {
        final boolean success =
                super.mouseClicked(mouseX, mouseY, mouseEvent)
                && !clone;
        final int index = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
        if (success) {
            this.delete(index);
        } else if (
                mouseEvent == 1
                        && index != -1
        ) {
            this.mc.displayGuiScreen(
                    new EditDirectoryMenu<>(
                            this.container,
                            mouseX,
                            mouseY,
                            this
                    )
            );
        }
        return success;
    }

    /**
     * Draws the list.
     *
     * @param mouseX       mouse pos x
     * @param mouseY       mouse pos y
     * @param partialTicks partial ticks
     */
    @Override
    public void drawScreen(
            final int mouseX,
            final int mouseY,
            final float partialTicks
    ) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GuiUtils.drawGradientRect(
                this.left,
                LIST_TOP,
                this.width,
                this.bottom,
                BACKGROUND_COLOR,
                BACKGROUND_COLOR,
                0
        );
    }

    /**
     * @param index Directory location index
     * @return Returns the list entry representation of the directory located
     * at the index of the currently displayed directory list.
     */
    @Override
    public IGuiListEntry getListEntry(final int index) {
        return getFolder().getDirectory(index);
    }

    /**
     * @return Returns the width of the directory list.
     */
    @Override
    public int getListWidth() {
        return this.width;
    }

    /**
     * @return Gets the number of displayed directories
     */
    @Override
    protected int getSize() {
        return currentDir.folders();
    }

    /**
     * @return Returns the x position of the scroll bar
     */
    @Override
    protected int getScrollBarX() {
        return this.right;
    }

    //:: IModifiableList
    //:::::::::::::::::::::::::::::://

    @Override
    public final void rename(final int entryIndex, final String name) {
        getFolder().renameDir(entryIndex, name.replaceAll("/", "_"));
    }

    @Override
    public final void delete(final int entryIndex) {
        getFolder().removeDir(entryIndex);
        save();
        this.container.refreshDirectoryEntryList();
    }

    @Override
    public final void changeDir(final int entryIndex) {
        //:: Todo: add ability to move directories
    }

    //-----This:----------------------------------------//

    /**
     * Sets the current directory to the base directory.
     */
    public final void gotoBase() {
        this.currentDir = base;
        this.currentPath = new LinkedList<>();
    }

    /**
     * Some functionality is not applicable to clone instances of this class.
     * This can be used to determine if the instance in use is a clone of
     * another instance.
     *
     * @return true if the object referenced is a deep copy.
     */
    public boolean isClone() {
        return this.clone;
    }

    /**
     * Sets the display height of the list container.
     *
     * @param height Sets teh height of the list.
     */
    public void setHeight(final int height) {
        this.bottom = height - LIST_OFFSET;
    }

    /**
     * Navigates through the base folder based on where the current
     * directory is located. Visually, this will move up a directory
     * level from the directory branch that has been moved into.
     */
    public void back() {
        if (!currentPath.isEmpty()) {
            currentPath.pop();
            currentDir = getFolder();
            if (!clone) {
                container.refreshDirectoryEntryList();
            }
        }
    }

    /**
     * Adds a folder to the base directory.
     * This folder is inserted to the current directory.
     *
     * @param name The name of the new directory.
     */
    public void addFolder(final String name) {
        getFolder().newFolder(name.replaceAll("/", "_"));
    }

    /**
     * Populates the directory list with the list entries that are currently
     * viewable.
     * @param entries The list entries being viewed.
     */
    public final void populateDirectoryList(final List<K> entries) {
        for (K entry : entries) {
            try {
                String populateTo = entry.getPathToDir();
                base.folderPath(populateTo).newEntry(entry);
            } catch (NoSuchElementException e) {
                entry.setPath(MOPMLiterals.BASE_DIR);
                if (entry instanceof WorldEntry) {
                    DirectoryTree.writeWorldToBase(entry.getMopmSaveFile());
                } else if (entry instanceof ServerEntry) {
                    DirectoryTree.writeServerToBase((ServerEntry) entry);
                }
            }
        }
    }

    /**
     * returns the folder entry that is currently in focus.
     *
     * @return Returns the current directory being browsed.
     */
    public DirectoryTree<K> getFolder() {
        String path = this.uniquePath();

        if (path.equals(MOPMLiterals.BASE_DIR)) {
            return base;
        }
        return base.folderPath(
                this.uniquePath()
                        .substring((MOPMLiterals.BASE_DIR + "/").length()
                        )
        );
    }

    /**
     * @return Returns a reference to the base directory.
     */
    public final DirectoryTree<K> getBaseFolder() {
        return this.base;
    }

    /**
     * returns the path of the currently displayed directory location.
     * use currentUniquePath() to get a functional path.
     *
     * @return returns a vanity path string.
     */
    public String currentPath() {
        Deque<String> temp = new ArrayDeque<>();
        temp.addAll(this.currentPath);
        try {
            String path = temp.removeLast();
            path = path.substring(0, path.lastIndexOf('#'));
            while (!temp.isEmpty()) {
                String append = temp.removeLast();
                path += "/" + append.substring(0, append.lastIndexOf('#'));
            }
            return MOPMLiterals.BASE_DIR_NAME + "/" + path;
        } catch (NoSuchElementException e) {
            return MOPMLiterals.BASE_DIR_NAME;
        }
    }

    /**
     * directories with the same name might conflict with using
     * the path for navigation. All directories have unique names
     * that should be used for navigation.
     *
     * @return returns a usable, navigable path
     */
    public String uniquePath() {
        Deque<String> temp = new LinkedList<>();
        temp.addAll(this.currentPath);
        try {
            String path = temp.removeLast();
            while (!temp.isEmpty()) {
                path += "/" + temp.removeLast();
            }
            return MOPMLiterals.BASE_DIR + "/" + path;
        } catch (NoSuchElementException e) {
            return MOPMLiterals.BASE_DIR;
        }
    }

    /**
     * Writes the directory tree to a file to persist through game sessions.
     *
     * @return Returns true if the directory tree was successfully saved.
     * Returns false if an error occurred during the saving process.
     */
    public boolean save() {
        return this.base.save(this.saveFile);
    }

    /**
     *
     */
    public void print() {
        References.LOG.info("\n" + this.base);
    }
}
