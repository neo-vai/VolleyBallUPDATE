/*
 * Copyright (C) Matthew Steglinski (SainttX) <matt@ipvp.org>
 * Copyright (C) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.ienumerable.volleyball.canvas.type;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.ienumerable.volleyball.canvas.ArrayIterator;
import ru.ienumerable.volleyball.canvas.Menu;
import ru.ienumerable.volleyball.canvas.mask.Mask;
import ru.ienumerable.volleyball.canvas.slot.DefaultSlot;
import ru.ienumerable.volleyball.canvas.slot.Slot;

/**
 * An abstract class that provides a skeletal implementation of the Menu 
 * interface.
 */
public abstract class AbstractMenu implements Menu  {

    private Menu parent;
    private boolean redraw;
    private DefaultSlot[] slots;
    private CloseHandler handler;
    private CursorDropHandler cursorDropHandler;
    private final Set<MenuHolder> holders = new HashSet<>();

    // Bukkit Inventory information
    protected String inventoryTitle;
    protected int inventorySlots;
    protected InventoryType inventoryType;

    protected AbstractMenu(String title, int inventorySlots, Menu parent, boolean redraw) {
        if (title == null) {
            title = InventoryType.CHEST.getDefaultTitle();
        }
        this.inventoryTitle = title;
        this.inventorySlots = inventorySlots;
        this.parent = parent;
        this.redraw = redraw;
        this.generateSlots();
    }
    
    protected AbstractMenu(String title, InventoryType type, Menu parent, boolean redraw) {
        Objects.requireNonNull(type, "type cannot be null");
        if (title == null) {
            title = type.getDefaultTitle();
        }
        this.inventoryTitle = title;
        this.inventoryType = type;
        this.parent = parent;
        this.redraw = redraw;
        this.generateSlots();
    }

    /**
     * Initial method called to fill the Slots of the menu
     */
    protected void generateSlots() {
        this.slots = new DefaultSlot[getDimensions().getArea()];
        for (int i = 0 ; i < slots.length ; i++) {
            this.slots[i] = new DefaultSlot(this, i);
        }
    }

    @Override
    public Optional<Menu> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public boolean isRedraw() {
        return redraw;
    }

    @Override
    public void open(Player viewer) {
        InventoryHolder currentInventory =
                viewer.getOpenInventory().getTopInventory().getHolder();
        if (currentInventory instanceof MenuHolder) {
            MenuHolder holder = (MenuHolder) currentInventory;
            Menu open = holder.getMenu();

            if (open == this) {
                return;
            }

            Inventory inventory;

            if (isRedraw() && open.getDimensions().equals(getDimensions())) {
                inventory = holder.getInventory();
                ((AbstractMenu) open).closedByPlayer(viewer, false);
            } else {
                open.close(viewer);
                inventory = createInventory(holder);
                holder.setInventory(inventory);
                viewer.openInventory(inventory);
            }

            updateInventoryContents(viewer, inventory);
            holder.setMenu(this);
            holders.add(holder);
        } else {
            // Create new MenuHolder for the player
            MenuHolder holder = new MenuHolder(viewer, this);
            Inventory inventory = createInventory(holder);
            updateInventoryContents(viewer, inventory);
            holder.setInventory(inventory);
            viewer.openInventory(inventory);
            holders.add(holder);
        }
    }

    private Inventory createInventory(InventoryHolder holder) {
        return inventoryType == null
                ? Bukkit.createInventory(holder, inventorySlots, inventoryTitle)
                : Bukkit.createInventory(holder, inventoryType, inventoryTitle);
    }

    private void updateInventoryContents(Player viewer, Inventory inventory) {
        for (Slot slot : slots) {
            inventory.setItem(slot.getIndex(), slot.getItem(viewer));
        }
        viewer.updateInventory();
    }

    @Override
    public Collection<Player> getViewers() {
        return getHolders().stream().map(MenuHolder::getViewer).collect(Collectors.toSet());
    }

    @Override
    public boolean isOpen(Player viewer) {
        InventoryHolder currentInventory =
                viewer.getOpenInventory().getTopInventory().getHolder();
        return currentInventory instanceof MenuHolder &&
                ((MenuHolder) currentInventory).getMenu() == this;
    }

    @Override
    public void close() {
        getViewers().forEach(this::close);
    }

    @Override
    public void close(Player viewer) {
        closedByPlayer(viewer, true);
        viewer.closeInventory();
    }

    @Override
    public void update() {
        getViewers().forEach(this::update);
    }

    @Override
    public void update(Player viewer) throws IllegalStateException {
        if (!isOpen(viewer)) {
            return;
        }

        InventoryHolder openInventory = viewer.getOpenInventory().getTopInventory().getHolder();
        updateInventoryContents(viewer, openInventory.getInventory());
    }

    public void closedByPlayer(Player viewer, boolean triggerCloseHandler) {
        if (!isOpen(viewer)) {
            return;
        }

        MenuHolder holder = (MenuHolder)
                viewer.getOpenInventory().getTopInventory().getHolder();
        holders.remove(holder);
        if (triggerCloseHandler) {
            getCloseHandler().ifPresent(h -> h.close(viewer, this));
        }
    }

    public Set<MenuHolder> getHolders() {
        return Collections.unmodifiableSet(holders);
    }

    @Override
    public List<Slot> getSlots() {
        return Arrays.asList(slots);
    }

    @Override
    public List<Slot> getSlots(Mask mask) {
        List<Slot> slots = new ArrayList<>();
        mask.getSlots().forEach(idx -> slots.add(getSlot(idx)));
        return slots;
    }

    @Override
    public Slot getSlot(int index) {
        return slots[index];
    }

    @Override
    public Slot getSlot(int row, int column) {
        int columns = getDimensions().getColumns();
        int firstRowIndex = (row - 1) * columns;
        int index = firstRowIndex + column - 1;
        return getSlot(index);
    }

    @Override
    public Iterator<Slot> iterator() {
        return new ArrayIterator<>(slots);
    }

    @Override
    public void clear() {
        for (Slot slot : slots) {
            slot.setItem((ItemStack) null);
        }
    }

    @Override
    public void clear(int index) {
        Slot slot = getSlot(index);
        slot.setItem((ItemStack) null);
    }

    @Override
    public void setCloseHandler(CloseHandler handler) {
        this.handler = handler;
    }

    @Override
    public Optional<CloseHandler> getCloseHandler() {
        return Optional.ofNullable(handler);
    }

    @Override
    public Optional<CursorDropHandler> getCursorDropHandler() {
        return Optional.ofNullable(cursorDropHandler);
    }

    @Override
    public void setCursorDropHandler(CursorDropHandler handler) {
        this.cursorDropHandler = handler;
    }

    /**
     * Abstract base class for builders of {@link Menu} types.
     * <p>
     * Builder instances are reusable; calling {@link #build()} will
     * generate a new Menu with identical features to the ones created before it.
     */
    public static abstract class Builder<T extends Builder<T>> implements Menu.Builder<T> {

        private final Dimension dimensions;
        private String title;
        private Menu parent;
        private boolean redraw;

        public Builder(Dimension dimensions) {
            this.dimensions = dimensions;
        }

        @Override
        public Dimension getDimensions() {
            return dimensions;
        }

        @Override
        public T title(String title) {
            this.title = title;
            return (T) this;
        }

        @Override
        public T parent(Menu parent) {
            this.parent = parent;
            return (T) this;
        }

        @Override
        public T redraw(boolean redraw) {
            this.redraw = redraw;
            return (T) this;
        }

        public String getTitle() {
            return title;
        }

        public Menu getParent() {
            return parent;
        }

        public boolean isRedraw() {
            return redraw;
        }
    }
}
