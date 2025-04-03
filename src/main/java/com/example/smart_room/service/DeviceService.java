package com.example.smart_room.service;

import com.example.smart_room.model.Device;
import com.example.smart_room.model.Strategy;
import com.example.smart_room.model.StrategyDevice;
import com.example.smart_room.repository.DeviceRepository;
import com.example.smart_room.repository.StrategyDeviceRepository;
import com.example.smart_room.repository.StrategyRepository;
import com.example.smart_room.request.DeviceStrategyDTO;
import com.example.smart_room.request.DeviceValueDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public List<Strategy> getAllStrategies() {
        return strategyRepository.findAll();
    }

    @Transactional
    public Strategy createStrategy(DeviceStrategyDTO dto) {
        Strategy strategy = new Strategy();
        strategy.setName(dto.getName());
        strategy.setDescription(dto.getDescription());
        strategy.setStatus(dto.getStatus());

        if (dto.getListDeviceValues() != null) {
            @NotNull List<StrategyDevice> strategyDevices = dto.getListDeviceValues().stream()
                    .map(deviceDto -> {
                        Device device = deviceRepository.findById(deviceDto.getDeviceId())
                                .orElseThrow(() -> new RuntimeException("Device not found"));
                        StrategyDevice strategyDevice = new StrategyDevice();
                        strategyDevice.setStrategy(strategy);
                        strategyDevice.setDevice(device);
                        strategyDevice.setValue(deviceDto.getValue());
                        return strategyDevice;
                    })
                    .collect(Collectors.toList());
            strategy.setStrategyDevices(strategyDevices);
        }

        return strategyRepository.save(strategy);
    }

    @Transactional
    public Strategy updateStrategy(Long id, DeviceStrategyDTO dto) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        strategy.setName(dto.getName());
        strategy.setDescription(dto.getDescription());
        strategy.setStatus(dto.getStatus());

        // Remove existing strategy devices
        strategy.getStrategyDevices().clear();

        // Add new strategy devices
        if (dto.getListDeviceValues() != null) {
            List<StrategyDevice> strategyDevices = dto.getListDeviceValues().stream()
                    .map(deviceDto -> {
                        Device device = deviceRepository.findById(deviceDto.getDeviceId())
                                .orElseThrow(() -> new RuntimeException("Device not found"));
                        StrategyDevice strategyDevice = new StrategyDevice();
                        strategyDevice.setStrategy(strategy);
                        strategyDevice.setDevice(device);
                        strategyDevice.setValue(deviceDto.getValue());
                        return strategyDevice;
                    })
                    .collect(Collectors.toList());
            strategy.setStrategyDevices(strategyDevices);
        }

        return strategyRepository.save(strategy);
    }

    @Transactional
    public void deleteStrategy(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
        strategyRepository.delete(strategy);
    }

    public Strategy getStrategy(Long id) {
        return strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
    }

    public Device updateDeviceStatus(Long id, Device.Status status) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setStatus(status);
        return deviceRepository.save(device);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    public Device createDevice(Device device) {
        return deviceRepository.save(device);
    }
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        deviceRepository.delete(device);
    }
    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }
    public Device updateDevice(Long id, Device device) {
        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        existingDevice.setName(device.getName());
        existingDevice.setType(device.getType());
        existingDevice.setStatus(device.getStatus());
        existingDevice.setLocation(device.getLocation());
        existingDevice.setOwnerId(device.getOwnerId());
        existingDevice.setRoomId(device.getRoomId());
        return deviceRepository.save(existingDevice);
    }
}
