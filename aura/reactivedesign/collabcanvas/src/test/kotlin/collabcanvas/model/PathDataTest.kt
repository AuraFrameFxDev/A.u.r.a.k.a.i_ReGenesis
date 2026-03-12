package collabcanvas.model

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.*
import org.junit.Test

class PathDataTest {

    @Test
    fun addPointShouldAddPointToTheList() {
        val initialPath = PathData()
        val point = Offset(10f, 20f)

        val updatedPath = initialPath.addPoint(point)

        assertEquals(1, updatedPath.points.size)
        assertEquals(point, updatedPath.points[0])
        assertFalse(updatedPath.isComplete)
    }

    @Test
    fun addPointShouldMaintainExistingPointsWhenAddingANewOne() {
        val point1 = Offset(10f, 20f)
        val point2 = Offset(30f, 40f)
        val initialPath = PathData(points = listOf(point1))

        val updatedPath = initialPath.addPoint(point2)

        assertEquals(2, updatedPath.points.size)
        assertEquals(point1, updatedPath.points[0])
        assertEquals(point2, updatedPath.points[1])
    }

    @Test
    fun addPointShouldWorkSequentially() {
        val p1 = Offset(1f, 1f)
        val p2 = Offset(2f, 2f)
        val p3 = Offset(3f, 3f)

        val path = PathData()
            .addPoint(p1)
            .addPoint(p2)
            .addPoint(p3)

        assertEquals(listOf(p1, p2, p3), path.points)
    }

    @Test
    fun completeShouldSetIsCompleteToTrue() {
        val initialPath = PathData()
        assertFalse(initialPath.isComplete)

        val completedPath = initialPath.complete()

        assertTrue(completedPath.isComplete)
    }

    @Test
    fun completeShouldNotAffectExistingPoints() {
        val point = Offset(10f, 20f)
        val initialPath = PathData(points = listOf(point))

        val completedPath = initialPath.complete()

        assertEquals(listOf(point), completedPath.points)
        assertTrue(completedPath.isComplete)
    }

    @Test
    fun completeShouldRemainTrueWhenCalledOnAlreadyCompletedPath() {
        val completedPath = PathData(isComplete = true)

        val stillCompleted = completedPath.complete()

        assertTrue(stillCompleted.isComplete)
    }
}
