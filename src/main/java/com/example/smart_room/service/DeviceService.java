package com.example.smart_room.service;

import com.example.smart_room.model.Device;
import com.example.smart_room.model.Strategy;
import com.example.smart_room.model.StrategyDevice;
import com.example.smart_room.repository.DeviceRepository;
import com.example.smart_room.repository.StrategyDeviceRepository;
import com.example.smart_room.repository.StrategyRepository;
import com.example.smart_room.request.DeviceStrategyDTO;
import com.example.smart_room.request.DeviceValueDTO;
import com.example.smart_room.response.DeviceStrategyResponseDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AdafruitService adafruitService;

    public List<DeviceStrategyResponseDTO> getAllDeviceStrategyResponses() {
        List<Strategy> strategies = strategyRepository.findAll();

        return strategies.stream().map(strategy -> {
            DeviceStrategyResponseDTO dto = new DeviceStrategyResponseDTO();
            dto.setName(strategy.getName());
            dto.setId(strategy.getId());
            dto.setDescription(strategy.getDescription());
            dto.setStatus(strategy.getStatus());
            dto.setStartTime(strategy.getStartTime()); // If you add a startTime to the entity later

            List<DeviceStrategyResponseDTO.DeviceInStrategyDTO> devices = strategy.getStrategyDevices().stream().map(sd -> {
                Device device = sd.getDevice();
                return new DeviceStrategyResponseDTO.DeviceInStrategyDTO(
                        device.getId(),
                        device.getName(),
                        device.getType(),
                        device.getStatus().name(),
                        sd.getValue(),
                        device.getDeviceKey()
                );
            }).collect(Collectors.toList());

            dto.setListDeviceValues(devices);
            return dto;
        }).collect(Collectors.toList());
    }

    public void runStrategy(Long strategyId, Long userId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new IllegalArgumentException("Strategy with ID " + strategyId + " not found."));

        List<StrategyDevice> devices = strategy.getStrategyDevices();
        if (devices == null || devices.isEmpty()) {
            throw new IllegalStateException("No devices associated with this strategy.");
        }

        for (StrategyDevice strategyDevice : devices) {
            try {
                Device device = strategyDevice.getDevice();
                if (device == null) {

                    continue;
                }

                String value = strategyDevice.getValue();
                String deviceKey = device.getDeviceKey();

                if (!adafruitService.sendCommandToDevice( deviceKey, value, userId)) {;
                    throw new RuntimeException("Failed to send command to device with ID " + device.getId());
                }
            } catch (Exception ex) {
                throw ex;
            }
        }
    }


    @Transactional
    public Strategy createStrategy(DeviceStrategyDTO dto) {
        Strategy strategy = new Strategy();
        strategy.setName(dto.getName());
        strategy.setDescription(dto.getDescription());
        strategy.setStatus(dto.getStatus());
        strategy.setStartTime(dto.getStartTime());

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
        strategy.setStartTime(dto.getStartTime());

        // Step 1: Collect updated device IDs
        List<Long> updatedDeviceIds = dto.getListDeviceValues().stream()
                .map(DeviceValueDTO::getDeviceId)
                .collect(Collectors.toList());

        List<StrategyDevice> strategyDevices = strategy.getStrategyDevices();
        System.out.println("Before" + strategyDevices.size());
        strategyDevices.removeIf(strategyDevice -> !updatedDeviceIds.contains(strategyDevice.getDevice().getId()));

        System.out.println("Remive" + strategyDevices.size());
        // Step 3: Add or update devices
        if (dto.getListDeviceValues() != null) {
            for (DeviceValueDTO deviceDto : dto.getListDeviceValues()) {
                Device device = deviceRepository.findById(deviceDto.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("Device not found"));

                // Check if the device is already associated with the strategy
                Optional<StrategyDevice> existingStrategyDeviceOpt = strategyDevices.stream()
                        .filter(strategyDevice -> strategyDevice.getDevice().getId().equals(deviceDto.getDeviceId()))
                        .findFirst();

                if (existingStrategyDeviceOpt.isPresent()) {
                    // If the device exists, update its value
                    StrategyDevice existingStrategyDevice = existingStrategyDeviceOpt.get();
                    existingStrategyDevice.setValue(deviceDto.getValue());
                } else {
                    // If the device doesn't exist, create a new association
                    StrategyDevice newStrategyDevice = new StrategyDevice();
                    newStrategyDevice.setStrategy(strategy);
                    newStrategyDevice.setDevice(device);
                    newStrategyDevice.setValue(deviceDto.getValue());
                    strategyDevices.add(newStrategyDevice);  // Add the new device association
                }
            }
        }
        System.out.println(strategy.getStrategyDevices().size());
        // Save the updated strategy with all associated devices
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
        existingDevice.setDeviceKey(device.getDeviceKey());
        return deviceRepository.save(existingDevice);
    }
}
