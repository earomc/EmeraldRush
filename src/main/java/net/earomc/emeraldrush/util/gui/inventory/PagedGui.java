package net.earomc.emeraldrush.util.gui.inventory;

import com.google.common.collect.Lists;
import net.earomc.emeraldrush.util.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Like a {@link ChestGui}, but with multiple pages that can be turned by the player.
 * Each page is a subclass of {@link ChestGui} by itself.
 * This class is basically tying together the pages so that guis like this can be created
 * and work as one entity.
 * <p>
 * Limitations: You cannot create a PagedGui and then add more pages. You have to create
 * the PagedGUI through the {@link Builder} where they are initialized with their
 * pageId. Additionally, each Page gets their titles set, which contain their pageId.
 * And because of Bukkit's inventory api (see: {@link Inventory}, there's no "setTitle" method, you define it in the beginning: {@link Bukkit#createInventory(InventoryHolder, InventoryType, String)}), you cannot change an inventories title once it's created.
 * I mean I could add some way to replace the page with a new Page with a new title,
 * but meh ... this class is complicated enough. Just create a new PagedGui then.
 * </p>
 */

public class PagedGui extends InventoryGui implements CloneableGui<PagedGui> {

    private final HashMap<Player, Integer> playerCurrentPageIdMap;
    private List<Page> pages;

    private PagedGui(String title) {
        this(title, new ArrayList<>());
    }

    private PagedGui(String title, List<Page> pages) {
        super(title);
        this.playerCurrentPageIdMap = new HashMap<>();
        this.pages = pages;
    }

    public static Builder newBuilder(String title) {
        return new Builder(title);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(PagedGui fromPreset) {
        return new Builder(fromPreset);
    }

    /**
     * Iterates through pages using two indexes. Index i and index k. Index i is relative to a single page,
     * while k is relative to the entire PagedGui. It is incremented until it matches the specified slot.
     *
     * @param slot A slot relative to the entire gui.
     * @return A {@link SearchResult} containing the page the slot is on and the slot that is relative to the page.
     */
    @NotNull
    public SearchResult mapToPageSlot(int slot) {
        validateSlotBoundaries(slot);
        Iterator<Page> iterator = pages.iterator();
        Page page = iterator.next();
        int i = 0, k = 0;
        while (iterator.hasNext()) {
            if (k == slot) {
                return new SearchResult(i, page);
            }
            if (!page.isInSlotBoundaries(i)) {
                page = iterator.next();
                i = -1;
            }
            k++;
            i++;
        }
        //noinspection ConstantConditions
        return null;
    }

    public static class SearchResult {

        private final int pageSlotId;
        private final Page page;

        public SearchResult(int pageSlotId, Page page) {
            this.pageSlotId = pageSlotId;
            this.page = page;
        }

        public int getPageSlotId() {
            return pageSlotId;
        }

        public Page getPage() {
            return page;
        }
    }

    @Override
    public void setClickable(int slot, ItemStack stack, ClickAction action) {
        int size = getSize();
        SearchResult result = mapToPageSlot(slot);
        result.getPage().setClickable(result.getPageSlotId(), stack, action);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        SearchResult result = mapToPageSlot(slot);
        result.getPage().setItem(result.getPageSlotId(), stack);
    }

    @Override
    public void addItems(ItemStack... stacks) {
        Page firstPageWithSpace = getFirstPageWithSpace();
        if (firstPageWithSpace == null) return;
        for (ItemStack stack : stacks) {
            if (!firstPageWithSpace.hasSpace()) {
                firstPageWithSpace = getFirstPageWithSpace();
                if (firstPageWithSpace == null) {
                    return;
                }
            }
            firstPageWithSpace.addItems(stack);
        }
    }

    @Override
    public void addItems(List<ItemStack> stacks) {
        this.addItems(stacks.toArray(new ItemStack[0]));
    }

    @Override
    public void addClickables(List<Clickable> clickables) {
        this.addClickables(clickables.toArray(new Clickable[0]));
    }

    @Override
    public void addClickable(Clickable clickable) {
        this.addClickable(clickable.itemStack(), clickable.action());
    }

    /**
     * Adds clickables to a page. When the page is full, it stops. If you want to add the right amount of
     * pages needed to fit a specific amount of clickables/items, use {@link Builder#makeSpace(int)}
     * @param clickables The clickables to be added.
     */
    @Override
    public void addClickables(Clickable... clickables) {
        Page firstPageWithSpace = getFirstPageWithSpace();
        if (firstPageWithSpace == null) return;
        for (Clickable clickable : clickables) {
            if (!firstPageWithSpace.hasSpace()) {
                firstPageWithSpace = getFirstPageWithSpace();
                if (firstPageWithSpace == null) {
                    return;
                }
            }
            firstPageWithSpace.addClickable(clickable);
        }
    }

    @Override
    public void addClickable(ItemStack stack, ClickAction action) {
        Page firstPageWithSpace = getFirstPageWithSpace();
        if (firstPageWithSpace != null) {
            firstPageWithSpace.addClickable(stack, action);
        } else throw new IllegalStateException("No free page available!");
    }

    @Override
    public void fill(ItemStack itemStack) {
        pages.forEach(page -> page.fill(itemStack));
    }

    @Override
    public void fill(ItemStack itemStack, ClickAction action) {
        pages.forEach(page -> page.fill(itemStack, action));
    }

    @Nullable
    public Page getFirstPageWithSpace() {
        return pages.stream().filter(Page::hasSpace).findFirst().orElse(null);
    }

    @Override
    public boolean hasSpace() {
        return getFirstPageWithSpace() != null;
    }


    @Override
    public int firstEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PagedGui clone() throws CloneNotSupportedException {
        return (PagedGui) super.clone();
    }

    public Page getPage(int index) {
        return pages.get(index);
    }

    protected List<Page> getPages() {
        return pages;
    }

    private void setPages(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    public void open(Player player) {
        openPage(player, 0);
    }

    public void openPage(Player player, int pageId) {
        Validate.isTrue(pageId >= 0, "Page id cannot be negative!");
        if (pageId < pages.size()) {
            Page page = pages.get(pageId);
            if (page != null) {
                page.open(player);
                setPageId(player, pageId);
            }
        }
    }


    @Nullable
    public Integer getCurrentPageId(Player player) {
        return playerCurrentPageIdMap.get(player);
    }

    protected void setPageId(Player player, int newPageId) {
        this.playerCurrentPageIdMap.put(player, newPageId);
    }

    public void openNextPage(Player player) {
        Integer currentPageId = getCurrentPageId(player);
        if (currentPageId != null) {
            openPage(player, currentPageId + 1);
        }
    }

    public void openPreviousPage(Player player) {
        Integer currentPageId = getCurrentPageId(player);
        if (currentPageId != null && currentPageId > 0) {
            openPage(player, currentPageId - 1);
        }
    }

    public int getPageAmount() {
        return pages.size();
    }

    public int getLastPageId() {
        return getPageAmount() - 1;
    }

    @Override
    protected void setTitle(String title) {
        throw new UnsupportedOperationException();
    }

    public boolean hasPages() {
        return !pages.isEmpty();
    }

    @Override
    public int getSize() {
        return pages.stream().mapToInt(Page::getSize).sum();
    }

    public static final class Builder {

        private final List<Page.PageBuilder> pages;
        private final LinkedList<Consumer<PagedGui>> actionQueue;
        private String title = "PagedGui";
        private int rows;

        private Builder() {
            this.pages = Lists.newArrayList();
            this.actionQueue = Lists.newLinkedList();
            rows(3);
        }

        private Builder(PagedGui gui) {
            this.pages = gui.getPages().stream().map(page -> new Page.PageBuilder(page, this)).collect(Collectors.toList());
            this.actionQueue = Lists.newLinkedList();
            rows(3);
        }

        private Builder(String title) {
            this();
            this.title = title;
        }

        public void queueGuiAction(Consumer<PagedGui> action) {
            actionQueue.add(action);
        }

        private void applyGuiActions(PagedGui gui) {
            while (!actionQueue.isEmpty()) {
                Consumer<PagedGui> poll = actionQueue.poll();
                poll.accept(gui);
            }
        }

        public Builder rows(int i) {
            Validate.isTrue(i > 0 && i < 6, "Rows can only be between 1 and 5.");
            this.rows = i;
            pages.forEach(pageBuilder -> pageBuilder.rows = i);
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Adds a page to the PagedGui you're building.
         * @return A new PageBuilder that represents the new {@link Page}.
         */
        public Page.PageBuilder addPage() {
            Page.PageBuilder pageBuilder = new Page.PageBuilder(this);
            queueGuiAction(ignored -> addPageBuilder(pageBuilder));
            return pageBuilder;
        }

        public Page.PageBuilder getPage(int index) {
            return pages.get(index);
        }

        public int getPageAmount() {
            return pages.size();
        }

        public int getLastPageId() {
            return getPageAmount() - 1;
        }

        public Builder addClickables(Clickable... clickables) {
            queueGuiAction(gui -> gui.addClickables(clickables));
            return this;
        }

        public Builder addClickables(List<Clickable> clickables) {
            queueGuiAction(gui -> gui.addClickables(clickables));
            return this;
        }

        public Builder addClickable(ItemStack stack, ClickAction action) {
            queueGuiAction(gui -> gui.addClickable(stack, action));
            return this;
        }

        public Builder setClickable(int slot, ItemStack stack, ClickAction action) {
            queueGuiAction(gui -> gui.setClickable(slot, stack, action));
            return this;
        }

        public Builder setItem(int slot, ItemStack stack) {
            queueGuiAction(gui -> gui.setItem(slot, stack));
            return this;
        }

        public Builder addItems(ItemStack... stacks) {
            queueGuiAction(gui -> gui.addItems(stacks));
            return this;
        }

        public Builder makeSpaceAndAddItems(ItemStack... stacks) {
            makeSpace(stacks.length);
            addItems(stacks);
            return this;
        }

        /**
         * Adds pages needed for the given amount of slots. It takes the amount of rows,
         * (that you can set in the builder) into account so that each page will be created
         * with the right amount of rows.
         * @param slots Amount of slots.
         * @return Returns itself.
         */
        public Builder makeSpace(int slots) {
            if (slots <= 0) return this;
            int pagesSlotsSum = pages.stream().mapToInt(Page.PageBuilder::getSlotAmount).sum();
            int slotsNeeded = slots - pagesSlotsSum;
            int pagesNeeded = (int) Math.ceil(slotsNeeded / (rows * 9d));

            //pagesNeeded - 1 to create the last page with fewer rows.
            for (int i = 0; i < pagesNeeded - 1; i++) {
                Page.PageBuilder pb = new Page.PageBuilder(this);
                addPageBuilder(pb);
            }

            pagesSlotsSum = pages.stream().mapToInt(Page.PageBuilder::getSlotAmount).sum();
            int slotsLeft = slots - pagesSlotsSum;
            Page.PageBuilder lastPageBuilder = new Page.PageBuilder(this);
            lastPageBuilder.slots(slotsLeft);
            addPageBuilder(lastPageBuilder);

            return this;
        }

        public Builder fill(ItemStack stack) {
            actionQueue.add(gui -> gui.fill(stack));
            return this;
        }

        public Builder fill(ItemStack stack, ClickAction action) {
            actionQueue.add(gui -> gui.fill(stack, action));
            return this;
        }

        public Builder setCloseAction(Consumer<Player> closeAction) {
            actionQueue.add(gui -> gui.setCloseAction(closeAction));
            return this;
        }

        public Builder setOpenAction(Consumer<Player> openAction) {
            actionQueue.add(gui -> gui.setOpenAction(openAction));
            return this;
        }

        private void addPageBuilder(Page.PageBuilder pageBuilder) {
            this.pages.add(pageBuilder);
        }


        public PagedGui build() {
            PagedGui gui = new PagedGui(this.title);
            List<Page> freshlyBuiltPages = this.pages.stream().map(pageBuilder -> pageBuilder.buildPage(gui)).collect(Collectors.toList());
            // Fresh ;)
            gui.setPages(freshlyBuiltPages);
            applyGuiActions(gui);
            return gui;
        }

    }

    public static class Page extends SingleInventoryGui {

        private final PagedGui parent;
        private final int pageId;
        private final Inventory inventory;
        private final int rows;


        private ItemStack nextPageItem;
        private ItemStack prevPageItem;

        private Page(PagedGui parent, int pageId, int rows, String title, ItemStack nextPageItem, ItemStack prevPageItem) {
            super(title);
            this.parent = parent;
            this.pageId = pageId;
            this.rows = rows;
            this.nextPageItem = nextPageItem;
            this.prevPageItem = prevPageItem;
            this.inventory = createInventory();
        }

        @Override
        public void open(Player player) {
            this.nextPageItem = new ItemBuilder.ItemEditor(nextPageItem).name(ChatColor.GREEN + "Next page").build();
            this.prevPageItem = new ItemBuilder.ItemEditor(prevPageItem).name(ChatColor.RED + "Previous page").build();

            if (!isLastPage()) {
                this.getUnsafe().setClickable((rows + 1) * 9 - 1, nextPageItem, (player1, context) -> this.parent.openNextPage(player1));
            }
            if (!isFirstPage()) {
                this.getUnsafe().setClickable((rows + 1) * 9 - 9, prevPageItem, (player1, context) -> this.parent.openPreviousPage(player1));
            }

            super.open(player);
        }

        private boolean isFirstPage() {
            return this.pageId == 0;
        }

        private boolean isLastPage() {
            return this.pageId == parent.getPageAmount() - 1;
        }

        private Inventory createInventory() {
            return Bukkit.createInventory(null, (rows + 1) * 9, title);
        }

        public int getPageId() {
            return pageId;
        }

        public int getRows() {
            return rows;
        }

        public ItemStack getNextPageItem() {
            return nextPageItem;
        }

        public ItemStack getPrevPageItem() {
            return prevPageItem;
        }

        public PagedGui getParent() {
            return parent;
        }

        @Override
        public int getSize() {
            return rows * 9;
        }

        @Nullable
        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public static final class PageBuilder {
            private final Builder guiBuilder;
            private final LinkedList<Consumer<Page>> pageActionQueue;
            private @NotNull
            Integer pageId;
            private String title;
            private int rows;
            private ItemStack nextPageItem;
            private ItemStack prevPageItem;
            private boolean hasPageIndicator = true;

            private PageBuilder(Builder guiBuilder) {
                this.guiBuilder = guiBuilder;
                this.pageId = guiBuilder.getLastPageId() + 1;
                this.title = guiBuilder.title;
                this.rows = guiBuilder.rows;
                this.pageActionQueue = Lists.newLinkedList();
            }

            private PageBuilder(Page page, Builder guiBuilder) {
                this(guiBuilder);
                this.pageId = page.getPageId();
                this.title = page.getTitle();
                this.rows = page.getRows();
                this.nextPageItem = page.getNextPageItem();
                this.prevPageItem = page.getPrevPageItem();
            }

            /**
             * Once the {@link PageBuilder} is finished, it will add itself to the {@link Builder} of the gui that the page is supposed to be in.
             * The {@link Builder} will take care of instantiating the {@link Page} itself.
             *
             * @return the {@link Builder} that the page is built within.
             */

            public Builder build() {
                guiBuilder.addPageBuilder(this);
                return this.guiBuilder;
            }

            /**
             * Queues an action that will be applied to the page once it's created.
             * It is bec
             * @param action A lambda expression that takes a page as an input.
             */

            private void queuePageAction(Consumer<Page> action) {
                pageActionQueue.add(action);
            }

            private void applyPageActions(Page page) {
                while (pageActionQueue.size() != 0) {
                    Consumer<Page> action = pageActionQueue.poll();
                    action.accept(page);
                }
            }

            public PageBuilder addClickable(ItemStack stack, ClickAction action) {
                queuePageAction(page -> page.addClickable(stack, action));
                return this;
            }

            public PageBuilder setClickable(int slot, ItemStack stack, ClickAction action) {
                queuePageAction(page -> page.setClickable(slot, stack, action));
                return this;
            }

            public PageBuilder setItem(int slot, ItemStack stack) {
                queuePageAction(page -> page.setItem(slot, stack));
                return this;
            }

            public PageBuilder addItem(ItemStack... stacks) {
                queuePageAction(page -> page.addItems(stacks));
                return this;
            }

            public PageBuilder fill(ItemStack stack) {
                queuePageAction(page -> page.fill(stack));
                return this;
            }

            public PageBuilder fill(ItemStack stack, ClickAction action) {
                queuePageAction(page -> page.fill(stack, action));
                return this;
            }

            public PageBuilder setCloseAction(Consumer<Player> closeAction) {
                queuePageAction(page -> page.setCloseAction(closeAction));
                return this;
            }

            public PageBuilder setOpenAction(Consumer<Player> openAction) {
                queuePageAction(page -> page.setOpenAction(openAction));
                return this;
            }

            private Page buildPage(PagedGui parent) {
                if (title == null) {
                    title = parent.getTitle();
                }
                if (hasPageIndicator) {
                    title = title + " " + (pageId + 1) + "/" + guiBuilder.getPageAmount();
                }
                if (prevPageItem == null) {
                    prevPageItem = new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 14).build();
                }
                if (nextPageItem == null) {
                    nextPageItem = new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 5).build();
                }
                Page newPage = new Page(parent, pageId, rows, title, nextPageItem, prevPageItem);
                applyPageActions(newPage);
                return newPage;
            }

            public PageBuilder title(String title) {
                this.title = title;
                return this;
            }

            /**
             * When calling this method, the page will not get a page indicator.
             * @return this
             */
            public PageBuilder noPageIndicator() {
                this.hasPageIndicator = false;
                return this;
            }

            /**
             * When calling this method, the page will get a page indicator.
             * @return this
             */
            public PageBuilder pageIndicator() {
                this.hasPageIndicator = true;
                return this;
            }

            /**
             * Will set the rows for this individual page.
             *
             * @param rows Amount of rows.
             * @return this.
             */

            public PageBuilder rows(int rows) {
                setRows(rows);
                return this;
            }

            /**
             * Will set the rows so that there is enough space to accommodate "amount of slots" items.
             *
             * @param slots Amount of slots needed.
             * @return this.
             */
            public PageBuilder slots(int slots) {
                validateSlotBoundaries(slots, 45);
                setRows((int) Math.ceil(slots / 9d));
                return this;
            }

            public PageBuilder nextPageItem(ItemStack itemStack) {
                this.nextPageItem = itemStack;
                return this;
            }

            public PageBuilder previousPageItem(ItemStack itemStack) {
                this.prevPageItem = itemStack;
                return this;
            }

            public int getRows() {
                return rows;
            }

            private void setRows(int i) {
                Validate.isTrue(i > 0 && i < 6, "Rows can only be between 1 and 5.");
                this.rows = i;
            }

            public int getSlotAmount() {
                return getRows() * 9;
            }
        }

    }

}
