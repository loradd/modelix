<?xml version="1.0" encoding="UTF-8"?>
<solution name="de.q60.mps.web.ui.svg" uuid="cceec75f-de6e-4ee7-bd91-29a3a99bfede" moduleVersion="0" pluginKind="PLUGIN_CORE" compileInMPS="true">
  <models>
    <modelRoot contentPath="${module}" type="default">
      <sourceRoot location="models" />
    </modelRoot>
    <modelRoot contentPath="${module}/lib" type="java_classes">
      <sourceRoot location="batik-1.7.1/batik-rasterizer.jar" />
      <sourceRoot location="batik-1.7.1/batik-slideshow.jar" />
      <sourceRoot location="batik-1.7.1/batik-squiggle.jar" />
      <sourceRoot location="batik-1.7.1/batik-svgpp.jar" />
      <sourceRoot location="batik-1.7.1/batik-ttf2svg.jar" />
      <sourceRoot location="batik-1.7.1/batik.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-anim.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-awt-util.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-bridge.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-codec.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-css.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-dom.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-ext.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-extension.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-gui-util.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-gvt.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-parser.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-script.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-svg-dom.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-svggen.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-swing.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-transcoder.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-util.jar" />
      <sourceRoot location="batik-1.7.1/lib/batik-xml.jar" />
      <sourceRoot location="batik-1.7.1/lib/js.jar" />
      <sourceRoot location="batik-1.7.1/extensions/batik-rasterizer-ext.jar" />
      <sourceRoot location="batik-1.7.1/extensions/batik-squiggle-ext.jar" />
      <sourceRoot location="java_websocket.jar" />
      <sourceRoot location="json-20160810.jar" />
      <sourceRoot location="gson-2.8.0.jar" />
      <sourceRoot location="commons-codec-1.10.jar" />
    </modelRoot>
  </models>
  <stubModelEntries>
    <stubModelEntry path="${module}/lib/batik-1.7.1/batik-rasterizer.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/batik-slideshow.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/batik-squiggle.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/batik-svgpp.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/batik-ttf2svg.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/batik.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-anim.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-awt-util.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-bridge.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-codec.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-css.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-dom.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-ext.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-extension.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-gui-util.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-gvt.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-parser.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-script.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-svg-dom.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-svggen.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-swing.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-transcoder.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-util.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/batik-xml.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/lib/js.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/extensions/batik-rasterizer-ext.jar" />
    <stubModelEntry path="${module}/lib/batik-1.7.1/extensions/batik-squiggle-ext.jar" />
    <stubModelEntry path="${module}/lib/java_websocket.jar" />
    <stubModelEntry path="${module}/lib/json-20160810.jar" />
    <stubModelEntry path="${module}/lib/gson-2.8.0.jar" />
    <stubModelEntry path="${module}/lib/commons-codec-1.10.jar" />
    <stubModelEntry path="${module}/lib/ui-client.jar" />
  </stubModelEntries>
  <sourcePath />
  <dependencies>
    <dependency reexport="false">1ed103c3-3aa6-49b7-9c21-6765ee11f224(MPS.Editor)</dependency>
    <dependency reexport="true">6354ebe7-c22a-4a0f-ac54-50b52ab9b065(JDK)</dependency>
    <dependency reexport="false">f0fff802-6d26-4d2e-b89d-391357265626(de.slisson.mps.hacks.editor)</dependency>
    <dependency reexport="false">498d89d2-c2e9-11e2-ad49-6cf049e62fe5(MPS.IDEA)</dependency>
    <dependency reexport="false">b0f8641f-bd77-4421-8425-30d9088a82f7(org.apache.commons)</dependency>
    <dependency reexport="false">6ed54515-acc8-4d1e-a16c-9fd6cfe951ea(MPS.Core)</dependency>
    <dependency reexport="false">8865b7a8-5271-43d3-884c-6fd1d9cfdd34(MPS.OpenAPI)</dependency>
    <dependency reexport="false">9a4afe51-f114-4595-b5df-048ce3c596be(jetbrains.mps.runtime)</dependency>
    <dependency reexport="false">23865718-e2ed-41b5-a132-0da1d04e266d(jetbrains.mps.ide.httpsupport.manager)</dependency>
  </dependencies>
  <languageVersions>
    <language slang="l:f3061a53-9226-4cc5-a443-f952ceaf5816:jetbrains.mps.baseLanguage" version="8" />
    <language slang="l:774bf8a0-62e5-41e1-af63-f4812e60e48b:jetbrains.mps.baseLanguage.checkedDots" version="0" />
    <language slang="l:443f4c36-fcf5-4eb6-9500-8d06ed259e3e:jetbrains.mps.baseLanguage.classifiers" version="0" />
    <language slang="l:fd392034-7849-419d-9071-12563d152375:jetbrains.mps.baseLanguage.closures" version="0" />
    <language slang="l:83888646-71ce-4f1c-9c53-c54016f6ad4f:jetbrains.mps.baseLanguage.collections" version="1" />
    <language slang="l:f2801650-65d5-424e-bb1b-463a8781b786:jetbrains.mps.baseLanguage.javadoc" version="2" />
    <language slang="l:760a0a8c-eabb-4521-8bfd-65db761a9ba3:jetbrains.mps.baseLanguage.logging" version="0" />
    <language slang="l:a247e09e-2435-45ba-b8d2-07e93feba96a:jetbrains.mps.baseLanguage.tuples" version="0" />
    <language slang="l:817e4e70-961e-4a95-98a1-15e9f32231f1:jetbrains.mps.ide.httpsupport" version="0" />
    <language slang="l:63650c59-16c8-498a-99c8-005c7ee9515d:jetbrains.mps.lang.access" version="0" />
    <language slang="l:fe9d76d7-5809-45c9-ae28-a40915b4d6ff:jetbrains.mps.lang.checkedName" version="0" />
    <language slang="l:ceab5195-25ea-4f22-9b92-103b95ca8c0c:jetbrains.mps.lang.core" version="1" />
    <language slang="l:446c26eb-2b7b-4bf0-9b35-f83fa582753e:jetbrains.mps.lang.modelapi" version="0" />
    <language slang="l:28f9e497-3b42-4291-aeba-0a1039153ab1:jetbrains.mps.lang.plugin" version="4" />
    <language slang="l:ef7bf5ac-d06c-4342-b11d-e42104eb9343:jetbrains.mps.lang.plugin.standalone" version="0" />
    <language slang="l:3a13115c-633c-4c5c-bbcc-75c4219e9555:jetbrains.mps.lang.quotation" version="1" />
    <language slang="l:7866978e-a0f0-4cc7-81bc-4d213d9375e1:jetbrains.mps.lang.smodel" version="13" />
    <language slang="l:c7fb639f-be78-4307-89b0-b5959c3fa8c8:jetbrains.mps.lang.text" version="0" />
    <language slang="l:9ded098b-ad6a-4657-bfd9-48636cfe8bc3:jetbrains.mps.lang.traceable" version="0" />
  </languageVersions>
  <dependencyVersions>
    <module reference="3f233e7f-b8a6-46d2-a57f-795d56775243(Annotations)" version="0" />
    <module reference="6354ebe7-c22a-4a0f-ac54-50b52ab9b065(JDK)" version="0" />
    <module reference="6ed54515-acc8-4d1e-a16c-9fd6cfe951ea(MPS.Core)" version="0" />
    <module reference="1ed103c3-3aa6-49b7-9c21-6765ee11f224(MPS.Editor)" version="0" />
    <module reference="498d89d2-c2e9-11e2-ad49-6cf049e62fe5(MPS.IDEA)" version="0" />
    <module reference="8865b7a8-5271-43d3-884c-6fd1d9cfdd34(MPS.OpenAPI)" version="0" />
    <module reference="742f6602-5a2f-4313-aa6e-ae1cd4ffdc61(MPS.Platform)" version="0" />
    <module reference="cceec75f-de6e-4ee7-bd91-29a3a99bfede(de.q60.mps.web.ui.svg)" version="0" />
    <module reference="f0fff802-6d26-4d2e-b89d-391357265626(de.slisson.mps.hacks.editor)" version="0" />
    <module reference="23865718-e2ed-41b5-a132-0da1d04e266d(jetbrains.mps.ide.httpsupport.manager)" version="0" />
    <module reference="9a4afe51-f114-4595-b5df-048ce3c596be(jetbrains.mps.runtime)" version="0" />
    <module reference="b0f8641f-bd77-4421-8425-30d9088a82f7(org.apache.commons)" version="0" />
  </dependencyVersions>
</solution>
