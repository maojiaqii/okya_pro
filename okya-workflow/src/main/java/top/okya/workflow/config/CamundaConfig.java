package top.okya.workflow.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.spring.boot.starter.configuration.CamundaProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import top.okya.workflow.listener.GlobalExecutionListenerPostParser;

/**
 * @author: maojiaqi
 * @Date: 2025/5/19 10:07
 * @describe: 全局配置
 */

@Configuration
public class CamundaConfig implements CamundaProcessEngineConfiguration {

    @Autowired
    private GlobalExecutionListenerPostParser globalExecutionListenerPostParser;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        processEngineConfiguration.getCustomPostBPMNParseListeners().add(globalExecutionListenerPostParser);
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {

    }
}
