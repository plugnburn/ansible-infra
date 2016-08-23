import jenkins.model.*
import java.util.logging.Logger
import hudson.security.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.dabsquared.gitlabjenkins.connection.*
import hudson.util.Secret

def instance = Jenkins.getInstance()

def credentials_store = instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

def tokenInst = new GitLabApiTokenImpl(CredentialsScope.GLOBAL, "{{ gitlab_user_token_id }}", 'GitLab Token', Secret.fromString("{{ gitlab_user_token }}"))
def result = credentials_store.addCredentials(Domain.global(), tokenInst)



def private_key = """{{ gitlab_private_key }}
"""

def ssh_credentials = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, "{{ gitlab_private_key_id }}", 'admin@example.com',
  new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(private_key), '', 'GitLab private key'
)

def result2 = credentials_store.addCredentials(Domain.global(), ssh_credentials)

GitLabConnectionConfig gitLabConfig = (GitLabConnectionConfig) Jenkins.getInstance().getDescriptor(GitLabConnectionConfig.class)
gitLabConfig.getConnections().add(new GitLabConnection('GitLab Default', "{{ gitlab_url }}", "{{ gitlab_user_token_id }}", true, 10, 10))

def mavenPluginExtension = instance.getExtensionList(hudson.tasks.Maven.DescriptorImpl.class)[0]
def asList = (mavenPluginExtension.installations as List)
asList.add(new hudson.tasks.Maven.MavenInstallation('M3', null, [new hudson.tools.InstallSourceProperty([new hudson.tasks.Maven.MavenInstaller("3.3.9")])]))
mavenPluginExtension.installations = asList
mavenPluginExtension.save()

instance.save()

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement

def jobDslScript = new File('/var/jenkins_home/jobs.groovy')
def workspace = new File('/var/jenkins_home')
def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)
new DslScriptLoader(jobManagement).runScript(jobDslScript.text)
