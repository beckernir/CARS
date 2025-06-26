
<!-- Sidebar JavaScript -->
<!-- Enhanced Sidebar JavaScript - High Priority -->
    (function() {
    // Ensure this script runs after DOM content loaded, regardless of when it's placed in the document
    function initializeSidebar() {
        // Make sure we're not re-initializing if already done
        if (window.sidebarInitialized) return;
        window.sidebarInitialized = true;

        // Use more specific selectors to avoid conflicts
        const container = document.querySelector('.sidebar-container');
        const toggle = document.getElementById('sidebarToggleMode');
        const sidebar = document.querySelector('.sidebar');
        const mainContent = document.querySelector('.main-content');

        // If elements don't exist, try again later or create them
        if (!container || !sidebar || !mainContent) {
            console.warn('SafeGuard Sidebar: Required elements not found, will retry');
            // Try again after a short delay
            setTimeout(initializeSidebar, 100);
            return;
        }

        // Override any existing event listeners by cloning and replacing elements
        if (toggle) {
            const newToggle = toggle.cloneNode(true);
            toggle.parentNode.replaceChild(newToggle, toggle);

            // Add our high-priority event listener
            newToggle.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();
                container.classList.toggle('auto-hide');
                mainContent.classList.toggle('collapsed');
                localStorage.setItem('sidebarAutoHide', container.classList.contains('auto-hide') ? 'true' : 'false');
            }, true); // Use capturing phase for highest priority
        }

        // Force load saved preference, overriding any other scripts
        const isAutoHide = localStorage.getItem('sidebarAutoHide') === 'true';
        if (isAutoHide) {
            // Remove classes first to avoid conflicts
            container.classList.remove('auto-hide');
            mainContent.classList.remove('collapsed');
            // Force reflow
            void container.offsetWidth;
            // Then add them back
            container.classList.add('auto-hide');
            mainContent.classList.add('collapsed');
        } else {
            container.classList.remove('auto-hide');
            mainContent.classList.remove('collapsed');
        }

        // Handle submenu clicks in mini mode - override any existing handlers
        document.querySelectorAll('.nav-link[data-bs-toggle="collapse"]').forEach(link => {
            const newLink = link.cloneNode(true);
            link.parentNode.replaceChild(newLink, link);

            newLink.addEventListener('click', function (e) {
                if (sidebar.classList.contains('mini')) {
                    e.preventDefault();
                    e.stopPropagation();
                    return false;
                }
            }, true); // Use capturing phase for highest priority
        });

        // Override responsive behavior with high-priority handler
        function handleResize() {
            if (window.innerWidth <= 768) {
                container.classList.add('auto-hide');
                mainContent.classList.add('collapsed');
                sidebar.classList.remove('mini');
            }
        }

        // Remove any existing resize handlers and add ours with highest priority
        window.removeEventListener('resize', handleResize);
        window.addEventListener('resize', handleResize, true);

        // Force initial responsive check
        handleResize();

        // Set up a MutationObserver to ensure our classes stay applied
        const observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                if (mutation.attributeName === 'class') {
                    const currentState = container.classList.contains('auto-hide');
                    if (currentState !== (localStorage.getItem('sidebarAutoHide') === 'true')) {
                        if (localStorage.getItem('sidebarAutoHide') === 'true') {
                            container.classList.add('auto-hide');
                            mainContent.classList.add('collapsed');
                        } else {
                            container.classList.remove('auto-hide');
                            mainContent.classList.remove('collapsed');
                        }
                    }
                }
            });
        });

        // Start observing the sidebar container for class changes
        observer.observe(container, {attributes: true});
    }

    // Run immediately if DOM is already loaded
    if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeSidebar);
} else {
    initializeSidebar();
}

    // Also run on window load as a fallback
    window.addEventListener('load', initializeSidebar);

    // Try to initialize after a short delay in case other scripts interfere
    setTimeout(initializeSidebar, 300);

    // And again after a longer delay to ensure it takes effect
    setTimeout(initializeSidebar, 1000);

})(); // Execute immediately
