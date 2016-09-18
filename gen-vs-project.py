# -*- coding: utf-8 -*-

from os import path
import os;
import uuid
import sys

OUTPUT_ROOT_DIR = "D:\\mysql-5.7.15"
SOURCE_ROOT_DIR = "D:\\mysql-5.7.15"

SOLUTION_NAME = "mysql-5.7.15"
PROJECT_NAME = "mysql-5.7.15"

VC_INCLUDE_DIR = ["D:\\rocksdb-master\\include", "D:\\rocksdb-master"]
VC_INCLUDE_DIR = ["D:\\mesos-1.0.1\\include"
    , "D:\\mesos-1.0.1\\3rdparty\\libprocess\\include",
                  "D:\\mesos-1.0.1\\3rdparty\\stout\\include",
                  "D:\\mesos-1.0.1\\3rdparty\\glog-0.3.3\\src\\windows"
    , "D:\\mesos-1.0.1\\3rdparty\\leveldb-1.4\\include"
    , "D:\\mesos-1.0.1\\3rdparty\\libev-4.22"
    , "D:\\mesos-1.0.1\\3rdparty\\protobuf-2.6.1\\src"
    , "D:\\mesos-1.0.1\\src"
    , "D:\\mesos-1.0.1\\3rdparty\\boost-1.53.0"]

VC_INCLUDE_DIR = ["D:\\mysql-5.7.15\\include", "D:\\mysql-5.7.15\\libbinlogevents\\include"]

print SOURCE_ROOT_DIR
print OUTPUT_ROOT_DIR

if not path.isdir(SOURCE_ROOT_DIR):
    sys.stderr.write("SOURCE_ROOT_DIR: %s is not a valid directory\n" % SOURCE_ROOT_DIR)
    sys.exit(1)
else:
    sys.stdout.write("SOURCE_ROOT_DIR: %s\n" % SOURCE_ROOT_DIR)

if not path.isdir(OUTPUT_ROOT_DIR):
    sys.stderr.write("OUTPUT_ROOT_DIR: %s is not a valid directory\n" % OUTPUT_ROOT_DIR)
    sys.exit(1)
else:
    sys.stdout.write("OUTPUT_ROOT_DIR: %s\n" % OUTPUT_ROOT_DIR)

sln_uuid = str(uuid.uuid4()).upper()
project_uuid = str(uuid.uuid4()).upper()

SOLUTION_CONTENT_TEMPLATE = """
Microsoft Visual Studio Solution File, Format Version 12.00
# Visual Studio 2013
VisualStudioVersion = 12.0.21005.1
MinimumVisualStudioVersion = 10.0.40219.1
Project("{%s}") = "%s", "%s.vcxproj", "{%s}"
EndProject
Global
	GlobalSection(SolutionConfigurationPlatforms) = preSolution
		Debug|Win32 = Debug|Win32
		Release|Win32 = Release|Win32
	EndGlobalSection
	GlobalSection(ProjectConfigurationPlatforms) = postSolution
		{%s}.Debug|Win32.ActiveCfg = Debug|Win32
		{%s}.Debug|Win32.Build.0 = Debug|Win32
		{%s}.Release|Win32.ActiveCfg = Release|Win32
		{%s}.Release|Win32.Build.0 = Release|Win32
	EndGlobalSection
	GlobalSection(SolutionProperties) = preSolution
		HideSolutionNode = FALSE
	EndGlobalSection
EndGlobal
"""

PROJECT_HEADER_TEMPLATE = """<?xml version="1.0" encoding="utf-8"?>
<Project DefaultTargets="Build" ToolsVersion="12.0" xmlns="http://schemas.microsoft.com/developer/msbuild/2003">
  <ItemGroup Label="ProjectConfigurations">
    <ProjectConfiguration Include="Debug|Win32">
      <Configuration>Debug</Configuration>
      <Platform>Win32</Platform>
    </ProjectConfiguration>
    <ProjectConfiguration Include="Release|Win32">
      <Configuration>Release</Configuration>
      <Platform>Win32</Platform>
    </ProjectConfiguration>
  </ItemGroup>
  <PropertyGroup Label="Globals">
    <ProjectGuid>{%s}</ProjectGuid>
    <RootNamespace>testproject</RootNamespace>
  </PropertyGroup>
  <Import Project="$(VCTargetsPath)\Microsoft.Cpp.Default.props" />
  <PropertyGroup Condition="'$(Configuration)|$(Platform)'=='Debug|Win32'" Label="Configuration">
    <ConfigurationType>Application</ConfigurationType>
    <UseDebugLibraries>true</UseDebugLibraries>
    <PlatformToolset>v120</PlatformToolset>
    <CharacterSet>MultiByte</CharacterSet>
  </PropertyGroup>
  <PropertyGroup Condition="'$(Configuration)|$(Platform)'=='Release|Win32'" Label="Configuration">
    <ConfigurationType>Application</ConfigurationType>
    <UseDebugLibraries>false</UseDebugLibraries>
    <PlatformToolset>v120</PlatformToolset>
    <WholeProgramOptimization>true</WholeProgramOptimization>
    <CharacterSet>MultiByte</CharacterSet>
  </PropertyGroup>
  <Import Project="$(VCTargetsPath)\Microsoft.Cpp.props" />
  <ImportGroup Label="ExtensionSettings">
  </ImportGroup>
  <ImportGroup Label="PropertySheets" Condition="'$(Configuration)|$(Platform)'=='Debug|Win32'">
    <Import Project="$(UserRootDir)\Microsoft.Cpp.$(Platform).user.props" Condition="exists('$(UserRootDir)\Microsoft.Cpp.$(Platform).user.props')" Label="LocalAppDataPlatform" />
  </ImportGroup>
  <ImportGroup Label="PropertySheets" Condition="'$(Configuration)|$(Platform)'=='Release|Win32'">
    <Import Project="$(UserRootDir)\Microsoft.Cpp.$(Platform).user.props" Condition="exists('$(UserRootDir)\Microsoft.Cpp.$(Platform).user.props')" Label="LocalAppDataPlatform" />
  </ImportGroup>
  <PropertyGroup Label="UserMacros" />
  <PropertyGroup Condition="'$(Configuration)|$(Platform)'=='Debug|Win32'">
    <IncludePath>%s;$(IncludePath)</IncludePath>
  </PropertyGroup>
  <ItemDefinitionGroup Condition="'$(Configuration)|$(Platform)'=='Debug|Win32'">
    <ClCompile>
      <WarningLevel>Level3</WarningLevel>
      <Optimization>Disabled</Optimization>
      <SDLCheck>true</SDLCheck>
    </ClCompile>
    <Link>
      <GenerateDebugInformation>true</GenerateDebugInformation>
    </Link>
  </ItemDefinitionGroup>
  <ItemDefinitionGroup Condition="'$(Configuration)|$(Platform)'=='Release|Win32'">
    <ClCompile>
      <WarningLevel>Level3</WarningLevel>
      <Optimization>MaxSpeed</Optimization>
      <FunctionLevelLinking>true</FunctionLevelLinking>
      <IntrinsicFunctions>true</IntrinsicFunctions>
      <SDLCheck>true</SDLCheck>
    </ClCompile>
    <Link>
      <GenerateDebugInformation>true</GenerateDebugInformation>
      <EnableCOMDATFolding>true</EnableCOMDATFolding>
      <OptimizeReferences>true</OptimizeReferences>
    </Link>
  </ItemDefinitionGroup>
"""

PROJECT_FOOTER_TEMPLATE = """
  <Import Project="$(VCTargetsPath)\Microsoft.Cpp.targets" />
  <ImportGroup Label="ExtensionTargets">
  </ImportGroup>
</Project>
"""

FILTER_CONTENT_HEADER = """<?xml version="1.0" encoding="utf-8"?>
<Project ToolsVersion="4.0" xmlns="http://schemas.microsoft.com/developer/msbuild/2003">
"""
FILTER_CONTENT_FOOTER = """
</Project>"""


def output_solution():
    with open(OUTPUT_ROOT_DIR + path.sep + SOLUTION_NAME + ".sln", "w") as solution_file:
        template_str = SOLUTION_CONTENT_TEMPLATE
        data = template_str % (
            sln_uuid, PROJECT_NAME, PROJECT_NAME, project_uuid, project_uuid, project_uuid, project_uuid, project_uuid)
        solution_file.writelines(data)


ALL_FILES = []
ALL_CPP_SOURCE_FILES = []
ALL_HEADER_FILES = []
FILTER_NS_MAP = {}


def load_files():
    for root, dirs, files in os.walk(SOURCE_ROOT_DIR):
        # project_file.writelines(project_data);
        for file in files:
            full_name = root + path.sep + file
            ALL_FILES.append(full_name)
            ext = path.splitext(file)[1]
            dirname = path.dirname(full_name)

            if dirname == SOURCE_ROOT_DIR:
                continue

            filter_name = dirname.replace(SOURCE_ROOT_DIR + path.sep, "")

            filters = filter_name.split(path.sep)

            total = len(filters)
            i = 0
            totalName = ''
            while i < total:
                tempname = filters[i]
                # print tempname
                totalName = totalName + tempname
                if not FILTER_NS_MAP.has_key(totalName):
                    filterns = str(uuid.uuid4())
                    FILTER_NS_MAP[totalName] = filterns

                totalName = totalName + path.sep
                i += 1

            if ext.lower() == ".cpp" or ext.lower() == ".cxx" or ext.lower() == ".cc" or ext.lower() == ".c":
                ALL_CPP_SOURCE_FILES.append(full_name)
            elif ext.lower() == ".hpp" or ext.lower() == ".hxx" or ext.lower() == ".h":
                ALL_HEADER_FILES.append(full_name)


SOURCE_ENABLE_COMPILE = """    <ClCompile Include="%s">
      <ExcludedFromBuild Condition="'$(Configuration)|$(Platform)'=='Debug|Win32'">false</ExcludedFromBuild>
    </ClCompile>
"""
HEADER_DISABLE_COMPILE="""    <ClInclude Include="%s">
      <ExcludedFromBuild Condition="'$(Configuration)|$(Platform)'=='Debug|Win32'">true</ExcludedFromBuild>
    </ClInclude>
"""

def output_project():
    project_data = PROJECT_HEADER_TEMPLATE % (project_uuid, ";".join(VC_INCLUDE_DIR))
    project_file = open(OUTPUT_ROOT_DIR + path.sep + PROJECT_NAME + ".vcxproj", "w");

    project_file.writelines(project_data)
    project_file.writelines("  <ItemGroup>\n")

    for cppfile in ALL_CPP_SOURCE_FILES:
        # line = "    <ClCompile Include=\"%s\" />\n" % cppfile
        line = SOURCE_ENABLE_COMPILE % cppfile
        project_file.writelines(line)

    project_file.writelines("  </ItemGroup>")
    project_file.writelines("  <ItemGroup>\n")
    for headerdile in ALL_HEADER_FILES:
        line = HEADER_DISABLE_COMPILE % headerdile
        project_file.writelines(line)

    project_file.writelines("  </ItemGroup>")
    project_file.writelines(PROJECT_FOOTER_TEMPLATE)
    project_file.close()


FILTER_ITEM_TEMPLATE = """
    <Filter Include="%s">
      <UniqueIdentifier>{%s}</UniqueIdentifier>
    </Filter>
"""

FILE_FILTER_ITEM = """
    <ClCompile Include="%s">
      <Filter>%s</Filter>
    </ClCompile>
"""

HEADER_FILTER_ITEM = """
    <ClInclude Include="%s">
      <Filter>%s</Filter>
    </ClInclude>
"""


def output_filters():
    filter_file = open(OUTPUT_ROOT_DIR + path.sep + PROJECT_NAME + ".vcxproj.filters", "w");
    filter_file.writelines(FILTER_CONTENT_HEADER)
    filter_file.writelines("  <ItemGroup>\n")

    for k, v in FILTER_NS_MAP.items():
        filter_item = FILTER_ITEM_TEMPLATE % (k, v)
        filter_file.writelines(filter_item)

    filter_file.writelines("  </ItemGroup>\n")

    filter_file.writelines("  <ItemGroup>")
    for fullname in ALL_CPP_SOURCE_FILES:
        file_name = str(fullname)
        filter_name = file_name;
        filter_name = filter_name.replace(SOURCE_ROOT_DIR + path.sep, "")
        filter_name = path.dirname(filter_name)
        line = FILE_FILTER_ITEM % (file_name, filter_name)
        filter_file.writelines(line)

    filter_file.writelines("  </ItemGroup>")

    filter_file.writelines("  <ItemGroup>")
    for fullname in ALL_HEADER_FILES:
        file_name = str(fullname)
        filter_name = file_name;
        filter_name = filter_name.replace(SOURCE_ROOT_DIR + path.sep, "")
        filter_name = path.dirname(filter_name)
        line = HEADER_FILTER_ITEM % (file_name, filter_name)
        filter_file.writelines(line)

    filter_file.writelines("  </ItemGroup>")

    filter_file.writelines(FILTER_CONTENT_FOOTER)
    filter_file.close()


load_files()
output_solution()
output_project()
output_filters()
