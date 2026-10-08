package com.example

import com.example.data.model.GateState
import com.example.data.model.ParkingLotState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkingTelemetryTest {

    @Test
    fun `test initial state is not showing data before bluetooth connection`() {
        val state = ParkingLotState()
        assertFalse(state.hasReceivedData)
        assertFalse(state.isConnected)
        assertFalse(state.slot1)
        assertFalse(state.slot2)
        assertFalse(state.slot3)
        assertEquals(0, state.totalOccupied)
        assertEquals(3, state.availableCount)
        assertEquals(0L, state.lastUpdated)
    }

    @Test
    fun `test received state calculations`() {
        val state = ParkingLotState(
            hasReceivedData = true,
            isConnected = true,
            slot1 = true,
            slot2 = false,
            slot3 = true,
            totalOccupied = 2,
            gateState = GateState.OPEN
        )

        assertEquals(3, state.totalSlots)
        assertEquals(2, state.totalOccupied)
        assertEquals(1, state.availableCount)
        assertFalse(state.isFull)
        assertEquals(GateState.OPEN, state.gateState)
    }

    @Test
    fun `test full occupancy calculations`() {
        val state = ParkingLotState(
            hasReceivedData = true,
            isConnected = true,
            slot1 = true,
            slot2 = true,
            slot3 = true,
            totalOccupied = 3,
            gateState = GateState.CLOSED,
            buzzerAlert = true
        )

        assertEquals(0, state.availableCount)
        assertTrue(state.isFull)
        assertEquals(GateState.CLOSED, state.gateState)
        assertTrue(state.buzzerAlert)
    }
}
