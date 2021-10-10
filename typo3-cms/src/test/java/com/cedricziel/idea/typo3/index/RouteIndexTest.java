package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.routing.RouteStub;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class RouteIndexTest extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/route";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        myFixture.addFileToProject("typo3conf/ext/foo/ext_emconf.php", "");
        myFixture.addFileToProject("typo3conf/ext/bar/ext_emconf.php", "");
        myFixture.copyFileToProject("Routes.php", "typo3conf/ext/foo/Configuration/Backend/Routes.php");
        myFixture.copyFileToProject("BackendRoutes.php", "typo3conf/ext/bar/Configuration/Backend/Routes.php");
        myFixture.copyFileToProject("ext_tables.php", "typo3conf/ext/bar/ext_tables.php");
    }

    public void testRoutesAreIndexed() {
        assertRouteExists("xMOD_tximpexp");
        assertRouteExists("login");
        assertRouteExists("login_frameset");
        assertRouteExists("file_newfolder");
    }

    public void testRouteStubsAreCorrectlyGenerated() {
        assertRouteStubEquals("file_newfolder", "/file/new", "\\TYPO3\\CMS\\Backend\\Controller\\File\\CreateFolderController", "mainAction", "private");
        assertRouteStubEquals("tce_db", "/record/commit", "\\TYPO3\\CMS\\Backend\\Controller\\SimpleDataHandlerController", "mainAction", "private");
        assertRouteStubEquals("login", "/login", "\\TYPO3\\CMS\\Backend\\Controller\\LoginController", "formAction", "public");
    }

    public void testRoutesFromExtTablesPhpAreIndexed() {
        assertRouteExists("web_list");
    }

    public void testRouteStubsFromExtTablesPhpAreIndexed() {
        assertRouteStubEquals("web_list", null, "\\TYPO3\\CMS\\Recordlist\\RecordList", "mainAction", "user,group");
    }

    public void testIssue288AjaxRoutesAreIndexed() {
        myFixture.copyFileToProject("AjaxRoutes.php", "typo3conf/ext/foo/Configuration/Backend/AjaxRoutes.php");

        assertRouteExists("ajax_core_requirejs");

        myFixture.testHighlighting("ajax_route_usage.php");

        assertRouteStubEquals("ajax_core_requirejs","/core/requirejs", "\\TYPO3\\CMS\\Core\\Controller\\RequireJsController", "retrieveConfiguration", "public" );
    }

    private void assertRouteStubEquals(String name, String path, String controllerClass, String action, String access) {
        RouteStub stub = RouteIndex.getRoute(myFixture.getProject(), name).iterator().next();

        assertEquals(name, stub.getName());
        assertEquals(path, stub.getPath());
        assertEquals(controllerClass, stub.getController());
        assertEquals(action, stub.getMethod());
        assertEquals(access, stub.getAccess());
    }

    private void assertRouteExists(String routeName) {
        assertTrue(RouteIndex.hasRoute(myFixture.getProject(), routeName));
    }
}
