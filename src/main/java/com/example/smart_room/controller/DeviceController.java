package com.example.smart_room.controller;

import com.example.smart_room.model.Device;
import com.example.smart_room.model.Strategy;
import com.example.smart_room.model.User;
import com.example.smart_room.request.DeviceStrategyDTO;
import com.example.smart_room.response.ApiResponse;
import com.example.smart_room.response.DeviceStrategyResponseDTO;
import com.example.smart_room.service.DeviceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@SecurityRequirement(name = "bearerAuth")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/strategy")
    public ApiResponse<?> createDevice(@RequestBody DeviceStrategyDTO deviceStrategyDTO) {
        try {
            Strategy strategy = deviceService.createStrategy(deviceStrategyDTO);
            return new ApiResponse<>(201, "Strategy created successfully", strategy);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Failed to create strategy: " + e.getMessage(), null);
        }
    }

    @PostMapping("/strategy/{id}/run")
    public ApiResponse<?> runStrategy(@PathVariable Long id, @AuthenticationPrincipal User userDetails) {
        try {
            deviceService.runStrategy(id, userDetails.getId());
            return new ApiResponse<>(200, "Strategy executed successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Failed to execute strategy: " + e.getMessage(), null);
        }
    }

    @GetMapping("/strategy")
    public ApiResponse<List<DeviceStrategyResponseDTO>> getAllStrategies() {
        try {

            return new ApiResponse<>(200, "Strategies retrieved successfully", deviceService.getAllDeviceStrategyResponses());
        } catch (Exception e) {
            return new ApiResponse<>(400, "Failed to retrieve strategies: " + e.getMessage(), null);
        }
    }
    @PutMapping("/strategy/{id}")
    public ApiResponse<Strategy> updateStrategy(@PathVariable Long id, @RequestBody DeviceStrategyDTO strategyDTO) {
        try {
            Strategy strategy = deviceService.updateStrategy(id, strategyDTO);
            return new ApiResponse<>(200, "Strategy updated successfully", strategy);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Failed to update strategy: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/strategy/{id}")
    public ApiResponse<Void> deleteStrategy(@PathVariable Long id) {
        try {
            deviceService.deleteStrategy(id);
            return new ApiResponse<>(200, "Strategy deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Failed to delete strategy: " + e.getMessage(), null);
        }
    }

    @GetMapping("/strategy/{id}")
    public ApiResponse<Strategy> getStrategy(@PathVariable Long id) {
        try {
            Strategy strategy = deviceService.getStrategy(id);
            return new ApiResponse<>(200, "Strategy retrieved successfully", strategy);
        } catch (Exception e) {
            return new ApiResponse<>(404, "Strategy not found: " + e.getMessage(), null);
        }
    }
    @GetMapping("/{id}")
    public ApiResponse<Device> getDeviceById(@PathVariable Long id) {
        try {
            Device device = deviceService.getDeviceById(id);
            return new ApiResponse<>(200, "Device retrieved successfully", device);
        } catch (Exception e) {
            return new ApiResponse<>(404, "Device not found: " + e.getMessage(), null);
        }
    }

    @GetMapping
    public ApiResponse<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return new ApiResponse<>(200, "Devices retrieved successfully", devices);
    }

    @PostMapping
    public ApiResponse<Device> createDevice(@RequestBody Device device) {
        Device createdDevice = deviceService.createDevice(device);
        return new ApiResponse<>(201, "Device created successfully", createdDevice);
    }

    @PutMapping("/{id}")
    public ApiResponse<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        try {
            Device updatedDevice = deviceService.updateDevice(id, device);
            return new ApiResponse<>(200, "Device updated successfully", updatedDevice);
        } catch (Exception e) {
            return new ApiResponse<>(404, "Device not found: " + e.getMessage(), null);
        }
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Device> updateDeviceStatus(@PathVariable Long id, @RequestBody Device.Status status ) {
        try {
            Device updatedDevice = deviceService.updateDeviceStatus(id, status);
            return new ApiResponse<>(200, "Device status updated successfully", updatedDevice);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Error updating device status: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return new ApiResponse<>(200, "Device deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>(404, "Device not found: " + e.getMessage(), null);
        }
    }

}
